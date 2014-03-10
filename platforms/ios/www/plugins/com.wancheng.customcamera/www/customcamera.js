cordova.define("com.wancheng.customcamera.CustomCamera", function(require, exports, module) { /**
 * This class provides access to the device camera.
 *
 * @constructor
 */



var customcamera = function() {
	this.successCallback = null;
	this.errorCallback = null;
	this.options = null;
};

/**
 * Gets a picture from source defined by "options.sourceType", and returns the
 * image as defined by the "options.destinationType" option.

 * The defaults are sourceType=CAMERA and destinationType=DATA_URL.
 *
 * @param {Function} successCallback
 * @param {Function} errorCallback
 * @param {Object} options
 */
customcamera.prototype.getPicture = function(successCallback, errorCallback,
		options) {

	// successCallback required
	if (typeof successCallback !== "function") {
		console.log("Camera Error: successCallback is not a function");
		return;
	}

	// errorCallback optional
	if (errorCallback && (typeof errorCallback !== "function")) {
		console.log("Camera Error: errorCallback is not a function");
		return;
	}

	if (options === null || typeof options === "undefined") {
		options = {};
	}
	if (options.quality === null || typeof options.quality === "undefined") {
		options.quality = 80;
	}
	if (options.maxResolution === null
			|| typeof options.maxResolution === "undefined") {
		options.maxResolution = 0;
	}

	if (options.targetWidth === null
			|| typeof options.targetWidth === "undefined") {
		options.targetWidth = -1;
	} else if (typeof options.targetWidth === "string") {
		var width = new Number(options.targetWidth);
		if (isNaN(width) === false) {
			options.targetWidth = width.valueOf();
		}
	}
	if (options.targetHeight === null
			|| typeof options.targetHeight === "undefined") {
		options.targetHeight = -1;
	} else if (typeof options.targetHeight === "string") {
		var height = new Number(options.targetHeight);
		if (isNaN(height) === false) {
			options.targetHeight = height.valueOf();
		}
	}
	
	if(options.destinationType === null || typeof options.destinationType === "undefined") {
		options.destinationType = 1;
	}
	
	if(options.sourceType === null || typeof options.sourceType === "undefined") {
		options.sourceType = 1;
	}
	
	if(options.encodingType === null || typeof options.encodingType === "undefined") {
		options.encodingType = 0;
	}

	if(options.mediaType === null || typeof options.mediaType === "undefined") {
		options.mediaType = 0;
	}

	if(options.popoverOptions === null || typeof options.popoverOptions === "undefined") {
		options.popoverOptions = null;
	}

	if(options.cameraDirection === null || typeof options.cameraDirection === "undefined") {
		options.cameraDirection = 0;
	}

    var allowEdit = !!options.allowEdit;
    var correctOrientation = !!options.correctOrientation;
    var saveToPhotoAlbum = !!options.saveToPhotoAlbum;

    var args = [options.quality, options.destinationType, options.sourceType, options.targetWidth, options.targetHeight, options.encodingType,
                options.mediaType, allowEdit, correctOrientation, saveToPhotoAlbum, options.popoverOptions, options.cameraDirection];
	

	cordova.exec(successCallback, errorCallback, "ForegroundCameraLauncher", "takePicture",
			args);
};

customcamera.prototype.getVideo = function(successCallback, errorCallback,
		options) {

	// successCallback required
	if (typeof successCallback !== "function") {
		console.log("Camera Error: successCallback is not a function");
		return;
	}

	// errorCallback optional
	if (errorCallback && (typeof errorCallback !== "function")) {
		console.log("Camera Error: errorCallback is not a function");
		return;
	}

	if (options === null || typeof options === "undefined") {
		options = {};
	}
	if (options.quality === null || typeof options.quality === "undefined") {
		options.quality = 80;
	}
	if (options.maxResolution === null
			|| typeof options.maxResolution === "undefined") {
		options.maxResolution = 0;
	}

	if (options.targetWidth === null
			|| typeof options.targetWidth === "undefined") {
		options.targetWidth = -1;
	} else if (typeof options.targetWidth === "string") {
		var width = new Number(options.targetWidth);
		if (isNaN(width) === false) {
			options.targetWidth = width.valueOf();
		}
	}
	if (options.targetHeight === null
			|| typeof options.targetHeight === "undefined") {
		options.targetHeight = -1;
	} else if (typeof options.targetHeight === "string") {
		var height = new Number(options.targetHeight);
		if (isNaN(height) === false) {
			options.targetHeight = height.valueOf();
		}
	}
	
	if(options.destinationType === null || typeof options.destinationType === "undefined") {
		options.destinationType = 1;
	}
	
	if(options.sourceType === null || typeof options.sourceType === "undefined") {
		options.sourceType = 1;
	}
	
	if(options.encodingType === null || typeof options.encodingType === "undefined") {
		options.encodingType = 0;
	}

	if(options.mediaType === null || typeof options.mediaType === "undefined") {
		options.mediaType = 0;
	}

	if(options.popoverOptions === null || typeof options.popoverOptions === "undefined") {
		options.popoverOptions = null;
	}

	if(options.cameraDirection === null || typeof options.cameraDirection === "undefined") {
		options.cameraDirection = 0;
	}

    var allowEdit = !!options.allowEdit;
    var correctOrientation = !!options.correctOrientation;
    var saveToPhotoAlbum = !!options.saveToPhotoAlbum;

    var args = [options.quality, options.destinationType, options.sourceType, options.targetWidth, options.targetHeight, options.encodingType,
                options.mediaType, allowEdit, correctOrientation, saveToPhotoAlbum, options.popoverOptions, options.cameraDirection];
	

	cordova.exec(successCallback, errorCallback, "ForegroundCameraLauncher", "takeVideo",
			args);
};

customcamera.prototype.getAudio = function(successCallback, errorCallback,
		options) {

	// successCallback required
	if (typeof successCallback !== "function") {
		console.log("Camera Error: successCallback is not a function");
		return;
	}

	// errorCallback optional
	if (errorCallback && (typeof errorCallback !== "function")) {
		console.log("Camera Error: errorCallback is not a function");
		return;
	}

	if (options === null || typeof options === "undefined") {
		options = {};
	}
	if (options.quality === null || typeof options.quality === "undefined") {
		options.quality = 80;
	}
	if (options.maxResolution === null
			|| typeof options.maxResolution === "undefined") {
		options.maxResolution = 0;
	}

	if (options.targetWidth === null
			|| typeof options.targetWidth === "undefined") {
		options.targetWidth = -1;
	} else if (typeof options.targetWidth === "string") {
		var width = new Number(options.targetWidth);
		if (isNaN(width) === false) {
			options.targetWidth = width.valueOf();
		}
	}
	if (options.targetHeight === null
			|| typeof options.targetHeight === "undefined") {
		options.targetHeight = -1;
	} else if (typeof options.targetHeight === "string") {
		var height = new Number(options.targetHeight);
		if (isNaN(height) === false) {
			options.targetHeight = height.valueOf();
		}
	}
	
	if(options.destinationType === null || typeof options.destinationType === "undefined") {
		options.destinationType = 1;
	}
	
	if(options.sourceType === null || typeof options.sourceType === "undefined") {
		options.sourceType = 1;
	}
	
	if(options.encodingType === null || typeof options.encodingType === "undefined") {
		options.encodingType = 0;
	}

	if(options.mediaType === null || typeof options.mediaType === "undefined") {
		options.mediaType = 0;
	}

	if(options.popoverOptions === null || typeof options.popoverOptions === "undefined") {
		options.popoverOptions = null;
	}

	if(options.cameraDirection === null || typeof options.cameraDirection === "undefined") {
		options.cameraDirection = 0;
	}

    var allowEdit = !!options.allowEdit;
    var correctOrientation = !!options.correctOrientation;
    var saveToPhotoAlbum = !!options.saveToPhotoAlbum;

    var args = [options.quality, options.destinationType, options.sourceType, options.targetWidth, options.targetHeight, options.encodingType,
                options.mediaType, allowEdit, correctOrientation, saveToPhotoAlbum, options.popoverOptions, options.cameraDirection];
	

	cordova.exec(successCallback, errorCallback, "ForegroundCameraLauncher", "takeAudio",
			args);
};

cordova.addConstructor(function() {
	if (typeof customcamera.camera === "undefined") {
		customcamera.camera = new Camera();
	}
});

customcamera.install = function()
{
    customcamera = new customcamera();
    return customcamera;
};


cordova.addConstructor(customcamera.install);
});
