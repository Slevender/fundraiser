import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SaleItemComponent } from './list/sale-item.component';
import { SaleItemDetailComponent } from './detail/sale-item-detail.component';
import { SaleItemUpdateComponent } from './update/sale-item-update.component';
import { SaleItemDeleteDialogComponent } from './delete/sale-item-delete-dialog.component';
import { SaleItemRoutingModule } from './route/sale-item-routing.module';
import { SaleItemShopComponent } from './shop/sale-item-shop.component';
import { CheckoutComponent } from './shop/checkout/checkout.component';

@NgModule({
  imports: [SharedModule, SaleItemRoutingModule],
  declarations: [
    SaleItemComponent,
    SaleItemDetailComponent,
    SaleItemShopComponent,
    CheckoutComponent,
    SaleItemUpdateComponent,
    SaleItemDeleteDialogComponent,
  ],
})
export class SaleItemModule {}
