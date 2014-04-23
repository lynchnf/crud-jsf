package com.psclistens.example.service;

import javax.persistence.EntityManager;

/**
 * This interface is used to create function objects encapsulating your data access code so that it can be executed by
 * methods in the {@code BaseService} class without <em>you</em> having to worry about all that tedious boilerplate
 * code.
 * <p>
 * Typically, every time you write a data access method you have a bunch of boilerplate code at the beginning to create
 * an {@code EntityManager} and begin an {@code EntityTransaction}. Then you do the stuff you're really interested in.
 * Finally you have more boilerplate code to commit your stuff, catch and re-throw any exception that your stuff might
 * have thrown, rollback (if there was an exception), and (in a finally block) close your {@code EntityManager}. If you
 * want to detect and join a {@code EntityTransaction} that's already active, your boilerplate code gets even more
 * complicated.
 * <p>
 * You <em>could</em> copy your boilerplate code into each data access method, but that would be a <em>Bad Thing</em>
 * &trade;. This interface allows you to code your stuff in a function object which you pass into the
 * {@code doInTransaction()} or {@code doWithEntityManager()} methods. It then executes the beginning boilerplate code,
 * your function, and finally the ending boilerplate code.
 * 
 * @param <T>
 *            The object type of whatever you're returning from the {@link DaoFunction#doFunction(EntityManager)
 *            doFunction} method.
 * @author LYNCHNF
 * @see BaseService
 */
public interface DaoFunction<T> {
    T doFunction(EntityManager entityManager) throws Exception;

    String getErrorMessage();
}