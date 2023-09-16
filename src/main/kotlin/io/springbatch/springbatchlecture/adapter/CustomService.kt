package io.springbatch.springbatchlecture.adapter

class CustomService {

    private var cnt = 0

    fun joinCustomer(): String {
        return ("item" + cnt++)
    }
}
