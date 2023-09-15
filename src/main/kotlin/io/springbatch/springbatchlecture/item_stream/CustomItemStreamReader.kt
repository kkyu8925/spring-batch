package io.springbatch.springbatchlecture.item_stream

import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader

class CustomItemStreamReader(
    private val items: List<String>
) : ItemStreamReader<String> {

    private var index = 0
    private var restart = false

    override fun read(): String? {
        var item: String? = null

        if (index < items.size) {
            item = items[index]
            index++
        }

        if (index == 6 && !restart) {
            throw RuntimeException("Restart is required.")
        }

        return item
    }

    override fun open(executionContext: ExecutionContext) {
        if (executionContext.containsKey("index")) {
            index = executionContext.getInt("index")
            restart = true
        } else {
            index = 0
            executionContext.put("index", index)
        }
    }

    override fun update(executionContext: ExecutionContext) {
        executionContext.put("index", index)
    }

    override fun close() {
        println("close")
    }
}
