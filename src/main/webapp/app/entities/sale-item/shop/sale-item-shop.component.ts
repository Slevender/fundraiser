import { Component, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISaleItem } from '../sale-item.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { EntityArrayResponseType, SaleItemService } from '../service/sale-item.service';
import { SaleItemDeleteDialogComponent } from '../delete/sale-item-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { HealthDetails } from '../../../admin/health/health.model';
import { HealthModalComponent } from '../../../admin/health/modal/health-modal.component';
import { CheckoutComponent } from './checkout/checkout.component';

@Component({
  selector: 'jhi-sale-item',
  templateUrl: './sale-item-shop.component.html',
  styleUrls: ['./sale-item-shop.component.scss'],
})
export class SaleItemShopComponent implements OnInit {
  saleItems?: ISaleItem[];
  isLoading = false;

  predicate = 'id';
  ascending = true;

  itemsPerPage = ITEMS_PER_PAGE;
  links: { [key: string]: number } = {
    last: 0,
  };
  page = 1;

  total = 0;
  basket: ISaleItem[] = [];

  constructor(
    protected saleItemService: SaleItemService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected parseLinks: ParseLinks,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal
  ) {}

  reset(): void {
    this.page = 1;
    this.total = 0;
    this.basket = [];
    this.saleItems = [];
    this.load();
  }

  loadPage(page: number): void {
    this.page = page;
    this.load();
  }

  trackId = (_index: number, item: ISaleItem): number => this.saleItemService.getSaleItemIdentifier(item);

  ngOnInit(): void {
    this.load();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(saleItem: ISaleItem): void {
    const modalRef = this.modalService.open(SaleItemDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.saleItem = saleItem;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending);
  }

  clickOnImage(saleItem: ISaleItem): void {
    this.addToBasket(saleItem);
    this.calculateTotal();
  }

  addToBasket(saleItem: ISaleItem): void {
    if (saleItem.price && saleItem.quantity && saleItem.quantity > 0) {
      this.basket.push(saleItem);
    }
  }

  calculateTotal(): void {
    this.total = 0;
    this.basket.forEach(item => {
      if (item.price) {
        this.total += item.price;
      }
    });
  }

  checkout(): void {
    const modalRef = this.modalService.open(CheckoutComponent);
    modalRef.componentInstance.basket = this.basket;
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.saleItems = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: ISaleItem[] | null): ISaleItem[] {
    const saleItemsNew = this.saleItems ?? [];
    if (data) {
      for (const d of data) {
        if (saleItemsNew.map(op => op.id).indexOf(d.id) === -1) {
          saleItemsNew.push(d);
        }
      }
    }
    return saleItemsNew;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
  }

  protected queryBackend(page?: number, predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.saleItemService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page = this.page, predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
