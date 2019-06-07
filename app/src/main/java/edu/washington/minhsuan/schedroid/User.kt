package edu.washington.minhsuan.schedroid

class User {
    var full_name: String = ""
    var phone_num: String = ""
    var username: String = ""
    var password: String = ""

    constructor()

    constructor(full_name: String, phone_num: String, username: String, password: String) {
        this.full_name = full_name
        this.phone_num = phone_num
        this.username = username
        this.password = password
    }

    override fun toString(): String {
        return "name: $full_name, phone: $phone_num, username: $username, password: $password"
    }

}