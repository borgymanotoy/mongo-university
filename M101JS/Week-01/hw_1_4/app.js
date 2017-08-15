var express = require('express'),
    app = express(),
    engines = require('consolidate'),
    bodyParser = require('body-parser'),
    MongoClient = require('mongodb').MongoClient,
    assert = require('assert');

app.engine('html', engines.nunjucks);
app.set('view engine', 'html');
app.set('views', __dirname + '/views');
app.use(bodyParser.urlencoded({ extended: true })); 

// Handler for internal server errors
function errorHandler(err, req, res, next) {
    console.error(err.message);
    console.error(err.stack);
    res.status(500).json({ error: err });
}

MongoClient.connect('mongodb://localhost:27017/m101js', function(err, db) {

    assert.equal(null, err);
    console.log("Successfully connected to MongoDB.");

    app.get('/', function(req, res, next) {
        res.render('main', {});
    });

    app.get('/movies', function(req, res, next) {
        var search = req.query.search;
        db.collection('hw1_4').find({title: {'$regex': search, '$options': 'i'}},{_id: -1, title:1, year:1},{year: -1}).toArray(function(err, docs) {
            res.json(docs);
        });
    });

    app.get('/status', function(req, res, next) {
        var msg = req.query.msg;
        res.render('main', { 'message': msg});
    });

    app.post('/register_movie', function(req, res, next) {
        var title = req.body.title;
        var year = req.body.year;
        var imdb = req.body.imdb;
        
        if ((title == '') || (year == '') || (imdb == '')) {
            next('Please provide an entry for all fields.');
        } else {
            console.log("[title]: " + title + "\t[year]: " + year + "\t[imdb]: " + imdb);

            db.collection('hw1_4').insertOne(
                { 'title': title, 'year': year, 'imdb': imdb },
                function (err, r) {
                    assert.equal(null, err);
                    res.redirect('/status?msg=Successfully added movie.');
                }
            );
        }
    });

    app.use(errorHandler);

    var server = app.listen(3000, function() {
        var port = server.address().port;
        console.log('Express server listening on port %s.', port);
    });
    
});