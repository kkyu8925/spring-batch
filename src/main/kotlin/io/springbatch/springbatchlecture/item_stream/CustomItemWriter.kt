package io.springbatch.springbatchlecture.item_stream

import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamWriter

class CustomItemWriter : ItemStreamWriter<String> {

    override fun open(executionContext: ExecutionContext) {
        println("CustomItemWriter open")
    }

    override fun update(executionContext: ExecutionContext) {
        println("CustomItemWriter update")
    }

    override fun close() {
        println("CustomItemWriter close")
    }

    override fun write(chunk: Chunk<out String>) {
        chunk.items.forEach { println(it) }
    }
}
