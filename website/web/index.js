var express = require('express');
var path = require('path');
var app = express();
var expressWs = require('express-ws')(app);

app.use(require('node-sass-middleware')({
    src: path.join(__dirname, '../src/main/resources/stylesheets'),
    dest: path.join(__dirname, "website/stylesheets"),
    prefix: "/public/stylesheets",
    indentedSyntax: false,
    sourceMap: false
}));

app.use('/public', express.static(path.join(__dirname, 'website')));

app.ws('/ws', function (ws, req) {
    ws.send("{\"function\":\"login\",\"param\":\"\"}");
    ws.send("{\"function\":\"addRobot\",\"param\":{\"id\":0,\"name\":\"Demo robot 1\",\"type\":\"Demobot\",\"version\":{\"major\":0,\"minor\":4,\"patch\":3,\"qualifier\":4,\"qualifierNumber\":0},\"color\":\"rgba(0, 0, 0, 0.0)\",\"speed\":-1.0,\"trim\":0.0,\"track\":{\"x\":0.0,\"y\":0.0},\"lineFollower\":{\"state\":\"UNAVAILABLE\",\"foreground\":\"rgba(0, 0, 0, 0.0)\",\"background\":\"rgba(0, 0, 0, 0.0)\"},\"energy\":{\"value\":0.8,\"state\":\"DISCHARGING\"},\"camera\":{\"available\":false,\"stream\":\"\"},\"kicker\":{\"available\":true}}}");
    ws.send("{\"function\":\"addRobot\",\"param\":{\"id\":1,\"name\":\"Demo robot 2\",\"type\":\"Demobot\",\"version\":{\"major\":0,\"minor\":0,\"patch\":0,\"qualifier\":4,\"qualifierNumber\":0},\"color\":\"rgba(0, 0, 0, 0.0)\",\"speed\":-1.0,\"trim\":0.0,\"track\":{\"x\":0.0,\"y\":0.0},\"lineFollower\":{\"state\":\"UNAVAILABLE\",\"foreground\":\"rgba(0, 0, 0, 0.0)\",\"background\":\"rgba(0, 0, 0, 0.0)\"},\"energy\":{\"value\":0.4,\"state\":\"CHARGING\"},\"camera\":{\"available\":true,\"stream\":\"\"},\"kicker\":{\"available\":true}}}");
    ws.send("{\"function\":\"addRobot\",\"param\":{\"id\":2,\"name\":\"Demo robot 3\",\"type\":\"Demobot\",\"version\":{\"major\":0,\"minor\":0,\"patch\":0,\"qualifier\":4,\"qualifierNumber\":0},\"color\":\"#FFC107\",\"speed\":-1.0,\"trim\":0.0,\"track\":{\"x\":0.0,\"y\":0.0},\"lineFollower\":{\"state\":\"UNAVAILABLE\",\"foreground\":\"rgba(0, 0, 0, 0.0)\",\"background\":\"rgba(0, 0, 0, 0.0)\"},\"energy\":{\"value\":0.0,\"state\":\"NO_BATTERY\"},\"camera\":{\"available\":false,\"stream\":\"\"},\"kicker\":{\"available\":false}}}");
    ws.send("{\"function\":\"addRobot\",\"param\":{\"id\":3,\"name\":\"Demo robot 4\",\"type\":\"Demobot\",\"version\":{\"major\":0,\"minor\":0,\"patch\":0,\"qualifier\":4,\"qualifierNumber\":0},\"color\":\"#F44336\",\"speed\":-1.0,\"trim\":0.0,\"track\":{\"x\":0.0,\"y\":0.0},\"lineFollower\":{\"state\":\"UNAVAILABLE\",\"foreground\":\"rgba(0, 0, 0, 0.0)\",\"background\":\"rgba(0, 0, 0, 0.0)\"},\"energy\":{\"value\":0.5,\"state\":\"UNKNOWN\"},\"camera\":{\"available\":false,\"stream\":\"\"},\"kicker\":{\"available\":true}}}");
    ws.send("{\"function\":\"addController\",\"param\":{\"id\":0,\"name\":\"Demo controller 1\",\"code\":\"6323\",\"type\":\"DESKTOP\",\"description\":\"\",\"color\":\"rgba(0, 0, 0, 0.0)\"}}");
    ws.send("{\"function\":\"addController\",\"param\":{\"id\":1,\"name\":\"Demo controller 2\",\"code\":\"4851\",\"type\":\"MOBIL\",\"description\":\"A random browser identifier\",\"color\":\"rgba(0, 0, 0, 0.0)\"}}");
    ws.send("{\"function\":\"bind\",\"param\":{\"controllerId\":1,\"robotId\":0}}");
    ws.send("{\"function\":\"addController\",\"param\":{\"id\":2,\"name\":\"Demo controller 3\",\"code\":\"\",\"type\":\"PHYSICAL\",\"description\":\"\",\"color\":\"#607D8B\"}}");
    ws.send("{\"function\":\"bind\",\"param\":{\"controllerId\":2,\"robotId\":3}}");
    ws.send("{\"function\":\"addController\",\"param\":{\"id\":3,\"name\":\"Demo controller 4\",\"code\":\"\",\"type\":\"UNKNOWN\",\"description\":\"\",\"color\":\"rgba(0, 0, 0, 0.0)\"}}");
    ws.send("{\"function\":\"bind\",\"param\":{\"controllerId\":3,\"robotId\":0}}");

    ws.on('message', function (msg) {
        if (msg.indexOf("login") >= 0) {
            return;
        }
        if (msg === "ping") {
            return;
        }
        console.log(msg)
    });
});

app.use(function (req, res) {
    res.status(200);
    res.sendFile(path.join(__dirname, 'website/index.html'));
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});