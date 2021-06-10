namespace my.bookshop;

entity Books {
  key ID : Integer;
  title  : String;
  stock  : Integer;
}

entity Suppliers {
  key ID : Integer;
  products : Association to many Products on
              products.supplier = $self;
}

////////////////////////////// aspects
aspect AbstractProducts {
	key ID : Integer;
	name : String(100);
	stock : Integer;
}

entity Products : AbstractProducts {

  supplier : Association to Suppliers;
}

////////////////////////////// view definitions
entity Foo as select from Products { ID, name }
where name like '%TV%'
order by name;
// projection on - guaranties OData compatibility but has less features
entity Foo2 as projection on Products { ID, name }
where name like '%TV%'
order by name;

///////////////////////////////// composition association
entity Orders {
  key ID : UUID;
  orderNo : String;
  currency_code : String(3);
  Items : Composition of many OrderItems
  on Items.parent = $self;
}
entity OrderItems {
  key ID : UUID;
  parent : Association to Orders;
  product : Association to Products;
  amount : Integer;
}

//////////////// own type definition
type Monetary {
  value : Decimal(10,3);
  currency_code : String(3);
}

entity Products2 {
  key ID : Integer;
  price : Monetary;
}

/////////////// structured entities with anonym types
entity Products3 {
  key ID : Integer;
  price : {
    value : Decimal(10, 3);
    currency_code : String(3);
  }
}

////////////////// enums
// named
type Category: String enum { internal; external }
// anonym
entity Products4 {
  key ID : Integer;
  category: Category;
  status : Integer enum {
    planned = 1;
    inProduction = 2;
    ready = 3;
    deprecated = 4;
  }
}
////////////////////////// arrays
// through type
type EmailAddresses: array of String;
entity Account {
  userName: String;
  emails : EmailAddresses;
}
// or directly
entity Emails {
  emails: array of String;
}

//////////////////////////// key words
entity Products5 {
  key ID : Integer; // key
  name : String(100) not null; // not null
  virtual margin : Decimal(9, 2); // not saved to DB but exposed through services
  currency_code : String(3) default 'EUR'; // default
  priority : Integer default 2;
}

////////////////////////////// entity extension
entity Buildings {
  key name : String;
  type : String;
  height : Integer;
}
extend Buildings with {
  age : Integer;
}

/////////////////// additional to namespace context definition
context sales {
  entity Products {key ID: Integer; name : String};
  
  context sup {
    entity Suppliers {key ID: Integer; name : String};
  }
}
