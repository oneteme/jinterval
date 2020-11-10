package org.usf.learn.node;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;

import org.usf.learn.node.Node.ModelIntInerval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor class ModelIntInerval<M> {
	
	static final Comparator<ModelIntInerval<?>> COMPARATOR = comparingInt(ModelIntInerval::getStart);
	
	final int start;
	final int exclusifEnd;
	final M model;
}