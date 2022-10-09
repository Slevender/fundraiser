import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISaleItem, NewSaleItem } from '../sale-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISaleItem for edit and NewSaleItemFormGroupInput for create.
 */
type SaleItemFormGroupInput = ISaleItem | PartialWithRequiredKeyOf<NewSaleItem>;

type SaleItemFormDefaults = Pick<NewSaleItem, 'id'>;

type SaleItemFormGroupContent = {
  id: FormControl<ISaleItem['id'] | NewSaleItem['id']>;
  name: FormControl<ISaleItem['name']>;
  price: FormControl<ISaleItem['price']>;
  quantity: FormControl<ISaleItem['quantity']>;
  type: FormControl<ISaleItem['type']>;
  image: FormControl<ISaleItem['image']>;
  imageContentType: FormControl<ISaleItem['imageContentType']>;
};

export type SaleItemFormGroup = FormGroup<SaleItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleItemFormService {
  createSaleItemFormGroup(saleItem: SaleItemFormGroupInput = { id: null }): SaleItemFormGroup {
    const saleItemRawValue = {
      ...this.getFormDefaults(),
      ...saleItem,
    };
    return new FormGroup<SaleItemFormGroupContent>({
      id: new FormControl(
        { value: saleItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(saleItemRawValue.name, {
        validators: [Validators.required],
      }),
      price: new FormControl(saleItemRawValue.price, {
        validators: [Validators.required],
      }),
      quantity: new FormControl(saleItemRawValue.quantity),
      type: new FormControl(saleItemRawValue.type, {
        validators: [Validators.required],
      }),
      image: new FormControl(saleItemRawValue.image, {
        validators: [Validators.required],
      }),
      imageContentType: new FormControl(saleItemRawValue.imageContentType),
    });
  }

  getSaleItem(form: SaleItemFormGroup): ISaleItem | NewSaleItem {
    return form.getRawValue() as ISaleItem | NewSaleItem;
  }

  resetForm(form: SaleItemFormGroup, saleItem: SaleItemFormGroupInput): void {
    const saleItemRawValue = { ...this.getFormDefaults(), ...saleItem };
    form.reset(
      {
        ...saleItemRawValue,
        id: { value: saleItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SaleItemFormDefaults {
    return {
      id: null,
    };
  }
}
