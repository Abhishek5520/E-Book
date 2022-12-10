package com.example.e_book.models

class ModelComment {

    var id: String = ""
    var bookId: String = ""
    var timestamp: String = ""
    var uid: String = ""
    var comment: String = ""

    constructor()

    constructor(id: String, bookId: String, timestamp: String, uid: String, comment: String) {
        this.id = id
        this.bookId = bookId
        this.timestamp = timestamp
        this.uid = uid
        this.comment = comment
    }


}