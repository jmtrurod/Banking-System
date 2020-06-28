# Bank System - Midterm Project

The goal of this project is to build a Banking System API.

The project will be developed using Spring framework in Java and will also be supported by two data bases: SQL database to save all data and MongoDB to save logs.

## Setup

To run this project locally do the following after cloning the project:

1. Create two databases: `bank` and `bank_test`
2. Run `mvn spring-boot:run` to launch the application
3. Create an Admin User in mySQL DB using the following code (admin as username and as password):
```mySQL
USE bank;
insert into user (dtype, username, password) values
("Admin","admin","$2a$10$mfW1eQhr5WVeWw1Pq2mZcu2fYG9QPK.gKPfA7amFIcAbroRaBAn3u");
insert into role (role, user_id) values
("ROLE_ADMIN", 1)
```
4. Go to http://localhost:8080/swagger-ui.html#/ to see the Swagger on the browser

![swagger](https://github.com/jmtrurod/Banking-System/blob/master/img/swagger_banking.PNG)
![swagger2](https://github.com/jmtrurod/Banking-System/blob/master/img/swagger_banking_2.PNG)

## API Documentation
### General Controller

| Method | Endpoint        |                    Response                     |
| ------ | --------------- | :---------------------------------------------: |
| POST    | /account      |        admin creates an account         |
| GET    | /account/{id}  |     account holder gets account data; logs with account holder username and password      |
| PATCH   | /account/unfreeze/{id}       |               admin unfreezes an account              |
| POST   | /credit/{id}   |          admin credits an account          |
| POST    | /debit/{id} | admin debits an account |
| POST  | /transfer/accountFrom/{idFrom}/accountTo/{idTo} | account holder makes a transference; logs with account holder username and password  |

#### POST - [ /account ]
Admin must be logged in. {_username_: **admin**; _password_: **admin**}.
Admin must be provide an AccountDto Object. When trying to create an account, the Http responses will guide to create a right account according to its specifications.
An example of AccountDto in Json is provided:

```Json
{
    "username":"jmtrurod",
    "password":"jmtrurodpass",
    "interestRate": 0.15,
    "balance": 6500,
    "secretKey": "asecretkey",
    "type": "Saving",
    "primaryOwner":{
        "name": "Pepe Trujillo",
        "dateOfBirth": "1996-03-09",
        "address":{
            "city": "Pamplona",
            "country": "Spain",
            "street": "MyStreet",
            "houseNumber":10,
            "zipCode": "85601"
        },
        "mailingAddress":{
            "city": "Salamanca",
            "country": "Spain",
            "street": "AnotherStreet",
            "houseNumber":15,
            "zipCode": "23410"
        }
    }
}
```

##### GET - [ /account/{id} ]
Account Holder must be logged in. Only if he/she is owner of the account with the id expressed in the url will be able to access to its information.
An example of response in Json is provided:

```Json
{
    "id": 1,
    "type": "SAVING",
    "status": "ACTIVE",
    "balance": 6500.00,
    "primaryOwnerName": "Pepe Trujillo",
    "secondaryOwnerName": null
}
```

##### PATCH - [ /account/unfreeze/{id} ]
Admin must be logged in. Admin only will be able to unfreeze account with Frozen status.

##### POST - [ /credit/{id} ] & [ /debit/{id} ]
Admin must be logged in. Admin must provide a Money in its body, which will only consist on a balance:

```Json
{
    "balance": 150.00
}
```

##### POST - [ /transfer/accountFrom/{idFrom}/accountTo/{idTo} ]
Account Holder must be logged in. To make a transference, Account Holder must be an owner of the Account with id _idFrom_. Both accounts must exist and Account Holder account must have enought money to complete the trasnference. Furthermore, fraude detection will me applied and will freeze the account in case:
 - Transactions made in 24 hours that total to more than 150% of the customers highest daily total transactions in any other 24 hour period.
 - More than 2 transactions occuring on a single account within a 1 second period.
 Money will be passed in the body of the Http request as it happened in the previous examples.

### Third-Party Controller

| Method | Endpoint        |                    Response                     |
| ------ | --------------- | :---------------------------------------------: |
| POST    | /third-party      |        admin creates a third-party         |
| POST    | /third-party/credit/{hashkey}  |     third party credits an account      |
| POST    | /third-party/debit/{hashkey}       |               third party debits an account             |

##### POST - [ /third-party ]
Admin must be logged in. Admin must provide a ThirdPartyDto in its body. An example in Json is provided:

```Json
{
    "username": "tarfa",
    "password": "romeo",
    "hashkey": "ahashkey"
}
```

##### POST - [ /third-party/credit/{hashkey} ] & [ /third-party/debit/{hashkey} ]
A Third Party must be logged in. Third Party must provide its hashkey in the url to be able to make any kind of transaction. Furthermore, must provide a ThirdPartyTransactionDto. An example in Json is provided:

```Json
{
    "amount": 250.00,
    "accountId": 3,
    "secretKey": "secret_key_of_account_with_id_3"
}
```


## Tools

- Java as the foundation to build the app's backend
- Spring JPA for data persistence
- MySQL to handle platform models and database
- MongoDB to save logs in the api

## Postman Collection
The following url provides a Postman collection so testing this API will be faster for the user:
https://www.getpostman.com/collections/d9259f9872503e84b7d3
