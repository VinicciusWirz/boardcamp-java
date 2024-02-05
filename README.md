# Boardcamp 🎲♟

In the age dominated by electronic games, a nostalgic proposal arises for enthusiasts of analog fun: Boardcamp, the ultimate solution for board game aficionados! 🎲

## About

Board games offer a unique experience, whether on PC, console, or mobile. However, the challenge faced by many is the price of these games. Boardcamp emerges as an answer, bringing the fun of board games back without breaking the bank. 💸

With an innovative approach, Boardcamp is a management system for board game rental services. Allowing you and your friends to enjoy a wide variety of games for a specified period, without the commitment of a hefty investment.

## Endpoints

<details>
<summary>Games endpoints</summary>
<ul>
<li>Get all registered games</li>
<details>
<summary>(GET "/games")</summary>
<ul>
<li>Get all games registered in database</li>
<li>Status: 200 (OK)</li>

```javascript
// response body
[
  {
    id: 1,
    name: "Banco Imobiliário",
    image: "http://",
    stockTotal: 3,
    pricePerDay: 1500,
  },
  {
    id: 2,
    name: "Detetive",
    image: "http://",
    stockTotal: 1,
    pricePerDay: 2500,
  },
];
```

</ul>
</details>
<br/>
<li>Register new game</li>
<details>
<summary>(POST "/games")</summary>
<ul>
<li>Register a new game in the database</li>
<details>

<summary>Exceptions</summary>
<ul>
<li>stockTotal and pricePerDay must be greater than 0 and not null, if failed, status 400 (BAD_REQUEST)</li>
<li>name must not be empty or null, if failed, status 400 (BAD_REQUEST)</li>
<li>name must be unique (not already existent in database), if failed, status 409 (CONFLICT)</li>
</ul>

</details>
<li>Status: 201 (CREATED)</li>
</li>

```javascript
// request body example:
{
  name: 'Banco Imobiliário',
  image: 'http://www.imagem.com.br/banco_imobiliario.jpg',
  stockTotal: 3,
  pricePerDay: 1500
}
```

```javascript
// response
{
  id: 1,
  name: 'Banco Imobiliário',
  image: 'http://www.imagem.com.br/banco_imobiliario.jpg',
  stockTotal: 3,
  pricePerDay: 1500
}
```

</ul>

</details>
</ul>
</details>

<br/>

<details>
<summary>Customers endpoints</summary>
<ul>
<li>Get one customer's information</li>
<details>
<summary>(GET "/customers/:id")</summary>
<ul>
<li>Get specific customer's information</li>
<details>

<summary>Exceptions</summary>
<ul>
<li>customer ID must exist, if failed, status 404 (NOT_FOUND)</li>
</ul>

</details>
<li>Status: 200 (OK)</li>
</li>

```javascript
// response
{
  id: 1,
  name: 'João Alfredo',
  cpf: '01234567890'
}
```

</ul>
</details>

<br/>

<li>Register new customer</li>
<details>
<summary>(POST "/customers")</summary>
<ul>
<li>Register a new costumer</li>
<details>

<summary>Exceptions</summary>
<ul>
<li>name must not be empty or null, if failed, status 400 (BAD_REQUEST)</li>
<li>cpf be 11 digits long, if failed, status 400 (BAD_REQUEST)</li>
<li>cpf must be unique (not already existent in database), if failed, status 409 (CONFLICT)</li>
</ul>

</details>
<li>Status: 201 (CREATED)</li>
</li>

```javascript
// request body example:
{
  name: 'João Alfredo',
  cpf: '01234567890'
}
```

```javascript
// response
{
  id: 1,
  name: 'João Alfredo',
  cpf: '01234567890'
}
```

</ul>
</details>

</ul>
</details>

<br/>

<details>
<summary>Rentals endpoints</summary>
<ul>
<li>Get all rentals</li>
<details>
<summary>(GET "/rentals")</summary>
<ul>
<li>Get all rentals in database, with customer and game related to the rental</li>
<li>Status: 200 (OK)</li>

```javascript
// response
[
  {
    id: 1,
    rentDate: '2021-06-20',
    daysRented: 3,
    returnDate: null,
    originalPrice: 4500,
    delayFee: 0,
    customer: {
      id: 1,
      name: 'João Alfredo',
		  cpf: '01234567890'
    },
    game: {
      id: 1,
		  name: 'Banco Imobiliário',
		  image: 'http://www.imagem.com.br/banco.jpg',
		  stockTotal: 3,
		  pricePerDay: 1500
    }
  },
  ...
]
```

</ul>
</details>

<br/>

<li>Register a new rental</li>
<details>
<summary>(POST "/rentals")</summary>
<ul>
<li>Register new rental</li>

<details>
<summary>Exceptions</summary>
<ul>
<li>daysRented must be a num greater than 0, if failed, status 400 (BAD_REQUEST)</li>
<li>gameId and customerId must not be null, if failed, status 400 (BAD_REQUEST)</li>
<li>gameId must refeer to an existing game, if failed, status 404 (NOT_FOUND)</li>
<li>customerId must refeer to an existing customer, if failed, status 404 (NOT_FOUND)</li>
<li>There must be enough games in stock to succed the rental, if failed, status 422 (UNPROCESSABLE_ENTITY)</li>
</ul>

</details>
<li>Status: 201 (CREATED)</li>

```javascript
// request body example:
  {
    customerId: 1,
    gameId: 1,
    daysRented: 3
  }
```

```javascript
// response
  {
    id: 1,
    rentDate: '2021-06-20',
    daysRented: 3,
    returnDate: null,
    originalPrice: 4500,
    delayFee: 0,
    customer: {
      id: 1,
      name: 'João Alfredo',
		  cpf: '01234567890'
    },
    game: {
      id: 1,
		  name: 'Banco Imobiliário',
		  image: 'http://www.imagem.com.br/banco.jpg',
		  stockTotal: 3,
		  pricePerDay: 1500
    }
  }
```

</ul>
</details>

<br/>

<li>Return a rental</li>
<details>
<summary>(PUT "/rentals/:id/return")</summary>
<ul>
<li>Return a rental</li>

<details>
<summary>Exceptions</summary>
<ul>
<li>ID must refeer to an existing open rental (not returned yet), if failed, status 404 (NOT_FOUND)</li>
<li>Rental must be open (not yet returned), if failed, status 422 (UNPROCESSABLE_ENTITY)</li>
</ul>

</details>
<li>Status: 200 (OK)</li>


```javascript
// response
  {
    id: 1,
    rentDate: '2021-06-20',
    daysRented: 3,
    returnDate: '2021-06-25', //updated date
    originalPrice: 4500,
    delayFee: 3000, //updated value
    customer: {
      id: 1,
      name: 'João Alfredo',
		  cpf: '01234567890'
    },
    game: {
      id: 1,
		  name: 'Banco Imobiliário',
		  image: 'http://www.imagem.com.br/banco.jpg',
		  stockTotal: 3,
		  pricePerDay: 1500
    }
  }
```

</ul>
</details>

</ul>
</details>

## Technologies
The following tools and frameworks were used in the construction of the project:
<p>
  <img style='margin: 5px;' src='https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white'/>
  <img style='margin: 5px;' src='https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot'/>
<img style='margin: 5px;' src='https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white'/>
  <img style='margin: 5px;' src='https://img.shields.io/badge/prettier-1A2C34?style=for-the-badge&logo=prettier&logoColor=F7BA3E'/>
  <img style='margin: 5px;' src='https://img.shields.io/badge/eslint-3A33D1?style=for-the-badge&logo=eslint&logoColor=white'/>
</p>

## How to use
1. Clone this repository
2. Setup your environment variables (.env)
3. Execute ApiApplication.java (located in src/main/java/com/boardcamp/api/) with jdk 17
