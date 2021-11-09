package com.zyl315.animehunter.execption

class IPCheckException : Exception {
    constructor() : super()

    constructor(message: String) : super(message)
}

class MaxRetryException : Exception {
    constructor() : super()

    constructor(message: String) : super(message)
}

class UnSupportPlayTypeException : Exception {
    constructor() : super()

    constructor(message: String) : super(message)
}

