package edu.mvcc.jcovey.avoidprojectiles.model;

/**
 * Immutable snapshot of a completed 100-point run.
 *
 * @param score final score
 * @param elapsedSeconds total completion time in seconds
 *
 * @author Jason A. Covey
 */
public record RunCompletion(int score, double elapsedSeconds) {
}
