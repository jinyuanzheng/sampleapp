cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/com.wancheng.customcamera/www/customcamera.js",
        "id": "com.wancheng.customcamera.CustomCamera",
        "clobbers": [
            "window.CustomCamera"
        ]
    },
    {
        "file": "plugins/org.devgirl.calendar/www/calendar.js",
        "id": "org.devgirl.calendar.Calendar",
        "clobbers": [
            "window.calendar"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.wancheng.customcamera": "0.1.0",
    "org.devgirl.calendar": "0.1.0"
}
// BOTTOM OF METADATA
});