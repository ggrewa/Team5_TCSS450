package edu.uw.tcss450.team5.holochat.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface PasswordValidator
        extends Function<String, Optional<PasswordValidator.ValidationResult>> {

    static PasswordValidator checkPwdLength() {
        return checkPwdLength(6);
    }

    static PasswordValidator checkPwdLength(int length) {
        return password ->
                Optional.of(password.length() > length ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INVALID_LENGTH);
    }

    static PasswordValidator checkPwdDigit() {
        return password ->
                Optional.of(checkStringContains(password, Character::isDigit) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_DIGIT);
    }

    static PasswordValidator checkPwdUpperCase() {
        return password ->
                Optional.of(checkStringContains(password, Character::isUpperCase) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_UPPER);
    }

    static PasswordValidator checkPwdLowerCase() {
        return password ->
                Optional.of(checkStringContains(password, Character::isLowerCase) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_LOWER);
    }

    static PasswordValidator checkPwdSpecialChar() {
        return checkPwdSpecialChar("@#$%&*!?");
    }

    static PasswordValidator checkPwdSpecialChar(String specialChars) {
        return password ->
                Optional.of(checkStringContains(password,
                        c -> specialChars.contains(Character.toString((char) c))) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_SPECIAL);
    }

    static PasswordValidator checkPwdDoNotInclude(String excludeChars) {
        return password ->
                Optional.of(!checkStringContains(password, //NOTE the !
                        c -> excludeChars.contains(Character.toString((char) c))) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INCLUDES_EXCLUDED);
    }

    static PasswordValidator checkExcludeWhiteSpace() {
        return password ->
                Optional.of(!checkStringContains(password, //NOTE the !
                        Character::isWhitespace) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INCLUDES_WHITESPACE);
    }

    static PasswordValidator checkClientPredicate(Predicate<String> theTest) {
        return password ->
                Optional.of(theTest.test(password) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_CLIENT_ERROR);
    }

    static boolean checkStringContains(String check, IntPredicate test) {
        return check.chars().filter(test).count() > 0;
    }

    default PasswordValidator and(PasswordValidator other) {
        return password -> this.apply(password)
                .flatMap(result -> result == ValidationResult.SUCCESS ?
                        other.apply(password) : Optional.of(result));
    }

    default PasswordValidator or(PasswordValidator other) {
        return password -> this.apply(password)
                .flatMap(result -> result == ValidationResult.SUCCESS ?
                        Optional.of(result): other.apply(password));
    }

    default void processResult(Optional<ValidationResult> result,
                               Runnable onSuccess,
                               Consumer<ValidationResult> onError) {
        if (result.isPresent()) {
            if (result.get() == ValidationResult.SUCCESS) {
                onSuccess.run();
            } else {
                onError.accept(result.get());
            }
        } else {
            throw new IllegalStateException("Nothing to process");
        }
    }

    enum ValidationResult {
        SUCCESS,
        PWD_INVALID_LENGTH,
        PWD_MISSING_DIGIT,
        PWD_MISSING_UPPER,
        PWD_MISSING_LOWER,
        PWD_MISSING_SPECIAL,
        PWD_INCLUDES_EXCLUDED,
        PWD_INCLUDES_WHITESPACE,
        PWD_CLIENT_ERROR
    }
}
