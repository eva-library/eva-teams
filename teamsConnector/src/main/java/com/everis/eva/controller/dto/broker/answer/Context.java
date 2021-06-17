/* eVA
* Version: 2.0
* copyright (c) 2018 everis Spain S.A
* * Date: 01 December 2018
* Author: everis bots@everis.com - Guilherme Ferreira Gomes, Guilherme Durazzo, Caio Soliani
* All rights reserved
*/

package com.everis.eva.controller.dto.broker.answer;

public class Context {

	private String previousNode;

	private String nextNode;

	public String getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(String previousNode) {
		this.previousNode = previousNode;
	}

	public String getNextNode() {
		return nextNode;
	}

	public void setNextNode(String nextNode) {
		this.nextNode = nextNode;
	}

	@Override
	public String toString() {
		return "Context [previousNode=" + previousNode + ", nextNode=" + nextNode + "]";
	}

}
