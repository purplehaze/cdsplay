using my.bookshop as my from '../db/data-model';

@path : 'catalog'
service CatalogService {
    // unbound function
    function sayHello(to: String) returns String;
    function sayKotlinHello(to: String) returns String;
    
    @readonly entity Buildings as projection on my.Buildings;
    //@readonly entity Books as projection on my.Books;

    // books with bound operations
    entity Books as projection on my.Books 
    actions {
        function getBooksCount(likeName : String) returns Integer;
        action reduceStock(amount : Integer);
    };

    action addBuilding(name : String, height : Integer) returns Buildings;
}