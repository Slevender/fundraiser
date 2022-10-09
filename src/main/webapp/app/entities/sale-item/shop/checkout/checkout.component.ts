import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISaleItem } from '../../sale-item.model';
import { SaleItemService } from '../../service/sale-item.service';

@Component({
  selector: 'jhi-checkout',
  templateUrl: './checkout.component.html',
})
export class CheckoutComponent implements OnInit {
  basket: ISaleItem[] = [];
  total = 0;
  returnAmount = 0;
  cashPaid = 0;

  constructor(
    protected saleItemService: SaleItemService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    private activeModal: NgbActiveModal
  ) {}

  trackId = (_index: number, item: ISaleItem): number => this.saleItemService.getSaleItemIdentifier(item);

  ngOnInit(): void {
    this.calculateTotal();
  }

  calculateTotal(): void {
    this.basket.forEach(item => {
      if (item.price) {
        this.total += item.price;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log(changes['cashPaid'].currentValue);
  }

  dismiss(): void {
    this.activeModal.dismiss();
  }
}
