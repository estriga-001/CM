package com.example.section3_android.model

/**
 * Data model class that maps the Dog API JSON response.
 * The API returns: { "message": "<image_url>", "status": "success" }
 */
class ImageItem {
    var message: String = ""
    var status: String = ""

    constructor()

    constructor(message: String, status: String) {
        this.message = message
        this.status = status
    }
}
