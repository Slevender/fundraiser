package ee.ddd.fundraiser.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ee.ddd.fundraiser.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleItem.class);
        SaleItem saleItem1 = new SaleItem();
        saleItem1.setId(1L);
        SaleItem saleItem2 = new SaleItem();
        saleItem2.setId(saleItem1.getId());
        assertThat(saleItem1).isEqualTo(saleItem2);
        saleItem2.setId(2L);
        assertThat(saleItem1).isNotEqualTo(saleItem2);
        saleItem1.setId(null);
        assertThat(saleItem1).isNotEqualTo(saleItem2);
    }
}
