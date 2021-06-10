package net.smart4life.cdsplay.handlers;

import java.util.Optional;
import java.util.stream.Stream;

import cds.gen.catalogservice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import net.smart4life.cdsplay.MessageKeys;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {
	private static Logger log = LoggerFactory.getLogger(CatalogServiceHandler.class);

	private final PersistenceService db;
	private final CqnAnalyzer analyzer;

	public CatalogServiceHandler( PersistenceService db, CdsModel model){
		this.db = db;
		this.analyzer = CqnAnalyzer.create(model);
	}

	// execute after every Books read
	@After(event = CdsService.EVENT_READ, entity = Books_.CDS_NAME)
	public void discountBooks(Stream<Books> books) {
		log.info("!!!!!!! discountBooks(...)");
		books.filter(b -> b.getTitle() != null && b.getStock() != null)
		.filter(b -> b.getStock() > 200)
		.forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
	}

	@On(event = SayHelloContext.CDS_NAME)
    public void sayHello(SayHelloContext ctx){
        ctx.setResult("Hello "+ctx.getTo());
    }

	@On(entity = Books_.CDS_NAME)
    public void getBooksCount(GetBooksCountContext ctx){
		log.info("!!!!!!! getBooksCount(likeName={})", ctx.getLikeName());
		Long cnt = db.run(Select.from(CatalogService_.BOOKS)).rowCount();
        ctx.setResult(cnt.intValue());
    }

	@On(event = AddBuildingContext.CDS_NAME)
    public void addBuilding(AddBuildingContext ctx){
		final String name = ctx.getName();
		final int height = ctx.getHeight();

		final Buildings building = Buildings.create();
		building.setAge(2);
		building.setType("Skyscraper");
		building.setName(name);
		building.setHeight(height);
		log.info("!!!!!!! addBuilding(building={})", building);
		Result res = db.run(Insert.into(Buildings_.CDS_NAME).entry(building));
		Buildings inserted = res.single(Buildings.class);
        ctx.setResult(inserted);
    }

	@On(entity = Books_.CDS_NAME)
    public void reduceStock(ReduceStockContext ctx){
		final Integer amount = ctx.getAmount();
		Integer bookId = (Integer) analyzer.analyze(ctx.getCqn()).targetKeys().get(Books.ID);

		log.info("!!!!!!! reduceStock(amount={}) on bookId={}", amount, bookId);

		Optional<Books> book = db.run(Select.from(CatalogService_.BOOKS).columns(Books_::stock).byId(bookId)).first(Books.class);

		book.orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, MessageKeys.BOOK_MISSING)
				.messageTarget(Books_.class, b -> b.ID()));

		int stock = book.map(Books::getStock).get();
		db.run(Update.entity(CatalogService_.BOOKS).byId(bookId).data(Books.STOCK, stock -= amount));
		
        //ctx.setResult(inserted);
		ctx.setCompleted();
    }

}