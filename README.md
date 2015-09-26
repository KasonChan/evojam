# Evojam #

An web application written in Scala with Play Framework serving the endpoints 
described below:

### Requirements ###

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

### Running the code locally ###

1. Download this repository
2. `Import` the project into IntllijJ IDEA.
3. Enter `sbt run` to run the code.
4. Enter `http://localhost:9000/` in your browser.
