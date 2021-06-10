package net.smart4life.cdsplay.handlers

import cds.gen.catalogservice.CatalogService_
import cds.gen.catalogservice.SayKotlinHelloContext
import com.sap.cds.services.handler.EventHandler
import com.sap.cds.services.handler.annotations.On
import com.sap.cds.services.handler.annotations.ServiceName
import org.springframework.stereotype.Component

@Component
@ServiceName(CatalogService_.CDS_NAME)
class CatalogServiceHandlerKt : EventHandler {

    @On(event = [SayKotlinHelloContext.CDS_NAME])
    fun sayKotlinHello(ctx: SayKotlinHelloContext) {
        ctx.result = "Hello ${ctx.to} from Kotlin"
    }
}