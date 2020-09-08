package io.segmentme.core.db.common

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

final class RandomOrder extends AbstractAnnotationDrivenExtension<RandomizedOrder> {

    @Override
    void visitSpecAnnotation(RandomizedOrder annotation, SpecInfo spec) {

        final Random random = new Random(System.nanoTime())

        final List<Integer> order = (0..spec.features.size()) as ArrayList

        Collections.shuffle(order, random)

        spec.features.each { it.executionOrder = order.pop() }
    }
}
