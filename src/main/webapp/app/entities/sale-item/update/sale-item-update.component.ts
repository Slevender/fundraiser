import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SaleItemFormService, SaleItemFormGroup } from './sale-item-form.service';
import { ISaleItem } from '../sale-item.model';
import { SaleItemService } from '../service/sale-item.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ItemType } from 'app/entities/enumerations/item-type.model';

@Component({
  selector: 'jhi-sale-item-update',
  templateUrl: './sale-item-update.component.html',
})
export class SaleItemUpdateComponent implements OnInit {
  isSaving = false;
  saleItem: ISaleItem | null = null;
  itemTypeValues = Object.keys(ItemType);

  editForm: SaleItemFormGroup = this.saleItemFormService.createSaleItemFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected saleItemService: SaleItemService,
    protected saleItemFormService: SaleItemFormService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ saleItem }) => {
      this.saleItem = saleItem;
      if (saleItem) {
        this.updateForm(saleItem);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('fundraiserApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const saleItem = this.saleItemFormService.getSaleItem(this.editForm);
    if (saleItem.id !== null) {
      this.subscribeToSaveResponse(this.saleItemService.update(saleItem));
    } else {
      this.subscribeToSaveResponse(this.saleItemService.create(saleItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISaleItem>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(saleItem: ISaleItem): void {
    this.saleItem = saleItem;
    this.saleItemFormService.resetForm(this.editForm, saleItem);
  }
}
