package ee.ddd.fundraiser.web.rest;

import ee.ddd.fundraiser.domain.SaleItem;
import ee.ddd.fundraiser.repository.SaleItemRepository;
import ee.ddd.fundraiser.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ee.ddd.fundraiser.domain.SaleItem}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SaleItemResource {

    private final Logger log = LoggerFactory.getLogger(SaleItemResource.class);

    private static final String ENTITY_NAME = "saleItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleItemRepository saleItemRepository;

    public SaleItemResource(SaleItemRepository saleItemRepository) {
        this.saleItemRepository = saleItemRepository;
    }

    /**
     * {@code POST  /sale-items} : Create a new saleItem.
     *
     * @param saleItem the saleItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleItem, or with status {@code 400 (Bad Request)} if the saleItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sale-items")
    public ResponseEntity<SaleItem> createSaleItem(@Valid @RequestBody SaleItem saleItem) throws URISyntaxException {
        log.debug("REST request to save SaleItem : {}", saleItem);
        if (saleItem.getId() != null) {
            throw new BadRequestAlertException("A new saleItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SaleItem result = saleItemRepository.save(saleItem);
        return ResponseEntity
            .created(new URI("/api/sale-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sale-items/:id} : Updates an existing saleItem.
     *
     * @param id the id of the saleItem to save.
     * @param saleItem the saleItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleItem,
     * or with status {@code 400 (Bad Request)} if the saleItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sale-items/{id}")
    public ResponseEntity<SaleItem> updateSaleItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleItem saleItem
    ) throws URISyntaxException {
        log.debug("REST request to update SaleItem : {}, {}", id, saleItem);
        if (saleItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SaleItem result = saleItemRepository.save(saleItem);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, saleItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sale-items/:id} : Partial updates given fields of an existing saleItem, field will ignore if it is null
     *
     * @param id the id of the saleItem to save.
     * @param saleItem the saleItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleItem,
     * or with status {@code 400 (Bad Request)} if the saleItem is not valid,
     * or with status {@code 404 (Not Found)} if the saleItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sale-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleItem> partialUpdateSaleItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleItem saleItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update SaleItem partially : {}, {}", id, saleItem);
        if (saleItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleItem> result = saleItemRepository
            .findById(saleItem.getId())
            .map(existingSaleItem -> {
                if (saleItem.getName() != null) {
                    existingSaleItem.setName(saleItem.getName());
                }
                if (saleItem.getPrice() != null) {
                    existingSaleItem.setPrice(saleItem.getPrice());
                }
                if (saleItem.getQuantity() != null) {
                    existingSaleItem.setQuantity(saleItem.getQuantity());
                }
                if (saleItem.getType() != null) {
                    existingSaleItem.setType(saleItem.getType());
                }
                if (saleItem.getImage() != null) {
                    existingSaleItem.setImage(saleItem.getImage());
                }
                if (saleItem.getImageContentType() != null) {
                    existingSaleItem.setImageContentType(saleItem.getImageContentType());
                }

                return existingSaleItem;
            })
            .map(saleItemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, saleItem.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-items} : get all the saleItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleItems in body.
     */
    @GetMapping("/sale-items")
    public ResponseEntity<List<SaleItem>> getAllSaleItems(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of SaleItems");
        Page<SaleItem> page = saleItemRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-items/:id} : get the "id" saleItem.
     *
     * @param id the id of the saleItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sale-items/{id}")
    public ResponseEntity<SaleItem> getSaleItem(@PathVariable Long id) {
        log.debug("REST request to get SaleItem : {}", id);
        Optional<SaleItem> saleItem = saleItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(saleItem);
    }

    /**
     * {@code DELETE  /sale-items/:id} : delete the "id" saleItem.
     *
     * @param id the id of the saleItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sale-items/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Long id) {
        log.debug("REST request to delete SaleItem : {}", id);
        saleItemRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
