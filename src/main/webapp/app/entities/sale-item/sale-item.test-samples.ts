import { ItemType } from 'app/entities/enumerations/item-type.model';

import { ISaleItem, NewSaleItem } from './sale-item.model';

export const sampleWithRequiredData: ISaleItem = {
  id: 49782,
  name: 'bandwidth',
  price: 39219,
  type: ItemType['SECOND_HAND_ITEM'],
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithPartialData: ISaleItem = {
  id: 10473,
  name: 'Representative driver Soft',
  price: 4571,
  quantity: 27571,
  type: ItemType['SECOND_HAND_ITEM'],
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithFullData: ISaleItem = {
  id: 85582,
  name: 'project Customer Roads',
  price: 90275,
  quantity: 14887,
  type: ItemType['EDIBLE'],
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithNewData: NewSaleItem = {
  name: 'index Planner green',
  price: 78391,
  type: ItemType['SECOND_HAND_ITEM'],
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
