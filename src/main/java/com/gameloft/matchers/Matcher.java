package com.gameloft.matchers;

public interface Matcher<T> {

    boolean matches(T argument);

}
