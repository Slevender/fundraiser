import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SaleItemFormService } from './sale-item-form.service';
import { SaleItemService } from '../service/sale-item.service';
import { ISaleItem } from '../sale-item.model';

import { SaleItemUpdateComponent } from './sale-item-update.component';

describe('SaleItem Management Update Component', () => {
  let comp: SaleItemUpdateComponent;
  let fixture: ComponentFixture<SaleItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let saleItemFormService: SaleItemFormService;
  let saleItemService: SaleItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SaleItemUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SaleItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SaleItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    saleItemFormService = TestBed.inject(SaleItemFormService);
    saleItemService = TestBed.inject(SaleItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const saleItem: ISaleItem = { id: 456 };

      activatedRoute.data = of({ saleItem });
      comp.ngOnInit();

      expect(comp.saleItem).toEqual(saleItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISaleItem>>();
      const saleItem = { id: 123 };
      jest.spyOn(saleItemFormService, 'getSaleItem').mockReturnValue(saleItem);
      jest.spyOn(saleItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: saleItem }));
      saveSubject.complete();

      // THEN
      expect(saleItemFormService.getSaleItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(saleItemService.update).toHaveBeenCalledWith(expect.objectContaining(saleItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISaleItem>>();
      const saleItem = { id: 123 };
      jest.spyOn(saleItemFormService, 'getSaleItem').mockReturnValue({ id: null });
      jest.spyOn(saleItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: saleItem }));
      saveSubject.complete();

      // THEN
      expect(saleItemFormService.getSaleItem).toHaveBeenCalled();
      expect(saleItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISaleItem>>();
      const saleItem = { id: 123 };
      jest.spyOn(saleItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(saleItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
