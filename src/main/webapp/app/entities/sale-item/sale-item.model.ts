import { ItemType } from 'app/entities/enumerations/item-type.model';

export interface ISaleItem {
  id: number;
  name?: string | null;
  price?: number | null;
  quantity?: number | null;
  type?: ItemType | null;
  image?: string | null;
  imageContentType?: string | null;
}

export type NewSaleItem = Omit<ISaleItem, 'id'> & { id: null };
