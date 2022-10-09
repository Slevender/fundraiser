import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISaleItem } from '../sale-item.model';
import { SaleItemService } from '../service/sale-item.service';

@Injectable({ providedIn: 'root' })
export class SaleItemRoutingResolveService implements Resolve<ISaleItem | null> {
  constructor(protected service: SaleItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISaleItem | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((saleItem: HttpResponse<ISaleItem>) => {
          if (saleItem.body) {
            return of(saleItem.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
