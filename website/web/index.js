var express = require('express');
var path = require('path');
var app = express();

app.use(require('node-sass-middleware')({
    src: path.join(__dirname, '../src/main/resources/stylesheets'),
    dest: path.join(__dirname, "website/stylesheets"),
    prefix: "/public/stylesheets",
    indentedSyntax: true,
    sourceMap: true
}));

app.get('/', function (req, res) {
    res.sendFile(path.join(__dirname, 'website/index.html'));
});

app.use('/public', express.static(path.join(__dirname, 'website')));

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});