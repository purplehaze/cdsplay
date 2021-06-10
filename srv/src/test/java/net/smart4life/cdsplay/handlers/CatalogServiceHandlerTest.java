package net.smart4life.cdsplay.handlers;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.persistence.PersistenceService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cds.gen.catalogservice.Books;
@SpringBootTest
public class CatalogServiceHandlerTest {

	@Autowired
	private PersistenceService db;

	@Autowired
	private CdsModel model;

	private CatalogServiceHandler handler = new CatalogServiceHandler(db, model);
	private Books book = Books.create();

	@Before
	public void prepareBook() {
		book.setTitle("title");
	}

	@Test
	public void testDiscount() {
		book.setStock(500);
		handler.discountBooks(Stream.of(book));
		assertEquals("title (discounted)", book.getTitle());
	}

	@Test
	public void testNoDiscount() {
		book.setStock(100);
		handler.discountBooks(Stream.of(book));
		assertEquals("title", book.getTitle());
	}

	@Test
	public void testNoStockAvailable() {
		handler.discountBooks(Stream.of(book));
		assertEquals("title", book.getTitle());
	}

}
