import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SaleItemComponent } from '../list/sale-item.component';
import { SaleItemDetailComponent } from '../detail/sale-item-detail.component';
import { SaleItemUpdateComponent } from '../update/sale-item-update.component';
import { SaleItemRoutingResolveService } from './sale-item-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';
import { SaleItemShopComponent } from '../shop/sale-item-shop.component';
import { CheckoutComponent } from '../shop/checkout/checkout.component';

const saleItemRoute: Routes = [
  {
    path: '',
    component: SaleItemComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SaleItemDetailComponent,
    resolve: {
      saleItem: SaleItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SaleItemUpdateComponent,
    resolve: {
      saleItem: SaleItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SaleItemUpdateComponent,
    resolve: {
      saleItem: SaleItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'shop',
    component: SaleItemShopComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    resolve: {
      saleItem: SaleItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    resolve: {
      saleItem: SaleItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(saleItemRoute)],
  exports: [RouterModule],
})
export class SaleItemRoutingModule {}
