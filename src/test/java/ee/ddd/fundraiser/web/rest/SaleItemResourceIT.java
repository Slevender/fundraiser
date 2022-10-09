package ee.ddd.fundraiser.web.rest;

import static ee.ddd.fundraiser.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ee.ddd.fundraiser.IntegrationTest;
import ee.ddd.fundraiser.domain.SaleItem;
import ee.ddd.fundraiser.domain.enumeration.ItemType;
import ee.ddd.fundraiser.repository.SaleItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link SaleItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleItemResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final ItemType DEFAULT_TYPE = ItemType.EDIBLE;
    private static final ItemType UPDATED_TYPE = ItemType.SECOND_HAND_ITEM;

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/sale-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleItemMockMvc;

    private SaleItem saleItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleItem createEntity(EntityManager em) {
        SaleItem saleItem = new SaleItem()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .quantity(DEFAULT_QUANTITY)
            .type(DEFAULT_TYPE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return saleItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleItem createUpdatedEntity(EntityManager em) {
        SaleItem saleItem = new SaleItem()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return saleItem;
    }

    @BeforeEach
    public void initTest() {
        saleItem = createEntity(em);
    }

    @Test
    @Transactional
    void createSaleItem() throws Exception {
        int databaseSizeBeforeCreate = saleItemRepository.findAll().size();
        // Create the SaleItem
        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isCreated());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeCreate + 1);
        SaleItem testSaleItem = saleItemList.get(saleItemList.size() - 1);
        assertThat(testSaleItem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSaleItem.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testSaleItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testSaleItem.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSaleItem.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testSaleItem.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createSaleItemWithExistingId() throws Exception {
        // Create the SaleItem with an existing ID
        saleItem.setId(1L);

        int databaseSizeBeforeCreate = saleItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleItemRepository.findAll().size();
        // set the field null
        saleItem.setName(null);

        // Create the SaleItem, which fails.

        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isBadRequest());

        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleItemRepository.findAll().size();
        // set the field null
        saleItem.setPrice(null);

        // Create the SaleItem, which fails.

        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isBadRequest());

        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleItemRepository.findAll().size();
        // set the field null
        saleItem.setType(null);

        // Create the SaleItem, which fails.

        restSaleItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isBadRequest());

        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSaleItems() throws Exception {
        // Initialize the database
        saleItemRepository.saveAndFlush(saleItem);

        // Get all the saleItemList
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    void getSaleItem() throws Exception {
        // Initialize the database
        saleItemRepository.saveAndFlush(saleItem);

        // Get the saleItem
        restSaleItemMockMvc
            .perform(get(ENTITY_API_URL_ID, saleItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleItem.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getNonExistingSaleItem() throws Exception {
        // Get the saleItem
        restSaleItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleItem() throws Exception {
        // Initialize the database
        saleItemRepository.saveAndFlush(saleItem);

        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();

        // Update the saleItem
        SaleItem updatedSaleItem = saleItemRepository.findById(saleItem.getId()).get();
        // Disconnect from session so that the updates on updatedSaleItem are not directly saved in db
        em.detach(updatedSaleItem);
        updatedSaleItem
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restSaleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSaleItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSaleItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
        SaleItem testSaleItem = saleItemList.get(saleItemList.size() - 1);
        assertThat(testSaleItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSaleItem.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testSaleItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testSaleItem.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSaleItem.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testSaleItem.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingSaleItem() throws Exception {
        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();
        saleItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleItem() throws Exception {
        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();
        saleItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleItem() throws Exception {
        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();
        saleItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSaleItemWithPatch() throws Exception {
        // Initialize the database
        saleItemRepository.saveAndFlush(saleItem);

        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();

        // Update the saleItem using partial update
        SaleItem partialUpdatedSaleItem = new SaleItem();
        partialUpdatedSaleItem.setId(saleItem.getId());

        partialUpdatedSaleItem
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSaleItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
        SaleItem testSaleItem = saleItemList.get(saleItemList.size() - 1);
        assertThat(testSaleItem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSaleItem.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testSaleItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testSaleItem.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSaleItem.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testSaleItem.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateSaleItemWithPatch() throws Exception {
        // Initialize the database
        saleItemRepository.saveAndFlush(saleItem);

        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();

        // Update the saleItem using partial update
        SaleItem partialUpdatedSaleItem = new SaleItem();
        partialUpdatedSaleItem.setId(saleItem.getId());

        partialUpdatedSaleItem
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSaleItem))
            )
            .andExpect(status().isOk());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
        SaleItem testSaleItem = saleItemList.get(saleItemList.size() - 1);
        assertThat(testSaleItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSaleItem.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testSaleItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testSaleItem.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSaleItem.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testSaleItem.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingSaleItem() throws Exception {
        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();
        saleItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(saleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleItem() throws Exception {
        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();
        saleItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(saleItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleItem() throws Exception {
        int databaseSizeBeforeUpdate = saleItemRepository.findAll().size();
        saleItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(saleItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleItem in the database
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSaleItem() throws Exception {
        // Initialize the database
        saleItemRepository.saveAndFlush(saleItem);

        int databaseSizeBeforeDelete = saleItemRepository.findAll().size();

        // Delete the saleItem
        restSaleItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SaleItem> saleItemList = saleItemRepository.findAll();
        assertThat(saleItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
