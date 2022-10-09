import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'sale-item',
        data: { pageTitle: 'Fundraiser' },
        loadChildren: () => import('./sale-item/sale-item.module').then(m => m.SaleItemModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
