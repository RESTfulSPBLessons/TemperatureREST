package com.antonromanov.temprest.Flux;

import reactor.core.publisher.Flux;

public class LearnFlux {

	public void Do() {

		Flux<Integer> ints = Flux.range(1, 3);
		ints.subscribe(i -> System.out.println(i));
	}

}
