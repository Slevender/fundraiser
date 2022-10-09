import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISaleItem, NewSaleItem } from '../sale-item.model';

export type PartialUpdateSaleItem = Partial<ISaleItem> & Pick<ISaleItem, 'id'>;

export type EntityResponseType = HttpResponse<ISaleItem>;
export type EntityArrayResponseType = HttpResponse<ISaleItem[]>;

@Injectable({ providedIn: 'root' })
export class SaleItemService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sale-items');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(saleItem: NewSaleItem): Observable<EntityResponseType> {
    return this.http.post<ISaleItem>(this.resourceUrl, saleItem, { observe: 'response' });
  }

  update(saleItem: ISaleItem): Observable<EntityResponseType> {
    return this.http.put<ISaleItem>(`${this.resourceUrl}/${this.getSaleItemIdentifier(saleItem)}`, saleItem, { observe: 'response' });
  }

  partialUpdate(saleItem: PartialUpdateSaleItem): Observable<EntityResponseType> {
    return this.http.patch<ISaleItem>(`${this.resourceUrl}/${this.getSaleItemIdentifier(saleItem)}`, saleItem, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISaleItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISaleItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSaleItemIdentifier(saleItem: Pick<ISaleItem, 'id'>): number {
    return saleItem.id;
  }

  compareSaleItem(o1: Pick<ISaleItem, 'id'> | null, o2: Pick<ISaleItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getSaleItemIdentifier(o1) === this.getSaleItemIdentifier(o2) : o1 === o2;
  }

  addSaleItemToCollectionIfMissing<Type extends Pick<ISaleItem, 'id'>>(
    saleItemCollection: Type[],
    ...saleItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const saleItems: Type[] = saleItemsToCheck.filter(isPresent);
    if (saleItems.length > 0) {
      const saleItemCollectionIdentifiers = saleItemCollection.map(saleItemItem => this.getSaleItemIdentifier(saleItemItem)!);
      const saleItemsToAdd = saleItems.filter(saleItemItem => {
        const saleItemIdentifier = this.getSaleItemIdentifier(saleItemItem);
        if (saleItemCollectionIdentifiers.includes(saleItemIdentifier)) {
          return false;
        }
        saleItemCollectionIdentifiers.push(saleItemIdentifier);
        return true;
      });
      return [...saleItemsToAdd, ...saleItemCollection];
    }
    return saleItemCollection;
  }
}
