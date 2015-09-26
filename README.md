# Evojam #

[![Build Status](https://travis-ci.org/KasonChan/evojam.svg)](https://travis-ci.org/KasonChan/evojam)

An web application written in Scala with Play Framework serving the endpoints 
described below:

### Requirement ###

#### Create a invitee ####

##### Request #####

###### Route ######

```
POST /invitation HTTP/1.1
```

###### Body ######

```
Content-Type: application/json;charset=utf-8
{
  "invitee": "John Smith",
  "email": "john@smith.mx"
}
```

##### Response #####

###### Status ######

```
HTTP/1.1 201 Created
```

#### Get all invitees ####

##### Request #####

###### Route ######

```
GET /invitation HTTP/1.1
```

##### Response #####

###### Status ######

```
HTTP/1.1 200 OK
```

###### Body ######

```
Content-Type: application/json;charset=utf-8
[
  {
     "invitee": "John Smith",
     "email": "john@smith.mx"
  }
]
```

### Development ###

This application is built with the following:

- [Scala](http://www.scala-lang.org/) version 2.11.5
- [SBT](http://www.scala-sbt.org/) version 0.13.7
- [Play framework](https://playframework.com/) version 2.3.8
- [MongoDB](https://www.mongodb.org/) version 2.6.10
- [ReactiveMongo](http://reactivemongo.org/) version 0.10.5.0.akka23

### Assumption ###

```
{
     "invitee": "John Smith",
     "email": "john@smith.mx"
}
```

- The request Json object must contain both invitee and email.
- The length of invitee must be between `1` and `50`.
- Email must be unique. The pattern must be ```([a-zA-Z0-9._]+)@([a-zA-Z0-9._]+)(\.)([a-zA-Z0-9]+)```.

### Running the code locally ###

1. Download this repository
2. `Import` the project into IntllijJ IDEA.
3. Enter `sbt run` to run the code.
4. Enter `http://localhost:9000/` in your browser.
