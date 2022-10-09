import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISaleItem } from '../sale-item.model';
import { SaleItemService } from '../service/sale-item.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './sale-item-delete-dialog.component.html',
})
export class SaleItemDeleteDialogComponent {
  saleItem?: ISaleItem;

  constructor(protected saleItemService: SaleItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.saleItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
