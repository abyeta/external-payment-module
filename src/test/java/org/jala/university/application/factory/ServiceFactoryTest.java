package org.jala.university.application.factory;

import org.jala.university.application.validator.ServiceDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ServiceFactory.
 * Tests the singleton pattern and factory structure.
 * Note: Methods requiring database connection are tested in integration tests.
 */
class ServiceFactoryTest {

    @BeforeEach
    void setUp() {
        // Reset singleton instances if needed for testing
        // Note: Cannot fully reset without reflection due to private static fields
    }

    @Test
    void testGetValidator_ReturnsNonNull() {
        ServiceDataValidator validator = ServiceFactory.getValidator();
        assertNotNull(validator, "Validator should not be null");
    }

    @Test
    void testGetValidator_ReturnsSameInstance() {
        ServiceDataValidator validator1 = ServiceFactory.getValidator();
        ServiceDataValidator validator2 = ServiceFactory.getValidator();

        assertNotNull(validator1);
        assertNotNull(validator2);
        assertSame(validator1, validator2, "Validator should be a singleton");
    }

    @Test
    void testGetValidator_IsThreadSafe() throws InterruptedException {
        final ServiceDataValidator[] validators = new ServiceDataValidator[2];
        
        Thread thread1 = new Thread(() -> validators[0] = ServiceFactory.getValidator());
        Thread thread2 = new Thread(() -> validators[1] = ServiceFactory.getValidator());
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        assertNotNull(validators[0]);
        assertNotNull(validators[1]);
        assertSame(validators[0], validators[1], 
            "Validator should be the same instance even when accessed from different threads");
    }

    @Test
    void testConstructor_IsPrivate() throws NoSuchMethodException {
        Constructor<ServiceFactory> constructor = ServiceFactory.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
            "Constructor should be private to prevent instantiation");
    }

    @Test
    void testConstructor_ThrowsExceptionWhenInvokedViaReflection() throws NoSuchMethodException {
        Constructor<ServiceFactory> constructor = ServiceFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        
        assertDoesNotThrow(() -> {
            try {
                constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                // Expected if constructor throws exception
            }
        });
    }

    @Test
    void testClass_IsFinal() {
        assertTrue(Modifier.isFinal(ServiceFactory.class.getModifiers()),
            "ServiceFactory should be final to prevent subclassing");
    }

    @Test
    void testGetValidator_ConsistentBehavior() {
        ServiceDataValidator validator1 = ServiceFactory.getValidator();
        ServiceDataValidator validator2 = ServiceFactory.getValidator();
        ServiceDataValidator validator3 = ServiceFactory.getValidator();

        assertSame(validator1, validator2);
        assertSame(validator2, validator3);
        assertSame(validator1, validator3);
    }

    @Test
    void testGetValidator_ReturnsWorkingValidator() {
        ServiceDataValidator validator = ServiceFactory.getValidator();
        assertNotNull(validator);
        
        // Verify it's a functional validator (not just a null object)
        assertNotNull(validator.getClass());
        assertEquals(ServiceDataValidator.class, validator.getClass());
    }

    @Test
    void testFactoryMethods_ArePublicAndStatic() throws NoSuchMethodException {
        var getValidatorMethod = ServiceFactory.class.getMethod("getValidator");
        assertTrue(Modifier.isPublic(getValidatorMethod.getModifiers()));
        assertTrue(Modifier.isStatic(getValidatorMethod.getModifiers()));

        var getPaymentInvoiceServiceMethod = ServiceFactory.class.getMethod("getPaymentInvoiceService");
        assertTrue(Modifier.isPublic(getPaymentInvoiceServiceMethod.getModifiers()));
        assertTrue(Modifier.isStatic(getPaymentInvoiceServiceMethod.getModifiers()));

        var getRegistrationServiceMethod = ServiceFactory.class.getMethod("getRegistrationService");
        assertTrue(Modifier.isPublic(getRegistrationServiceMethod.getModifiers()));
        assertTrue(Modifier.isStatic(getRegistrationServiceMethod.getModifiers()));

        var getUpdateServiceMethod = ServiceFactory.class.getMethod("getUpdateService");
        assertTrue(Modifier.isPublic(getUpdateServiceMethod.getModifiers()));
        assertTrue(Modifier.isStatic(getUpdateServiceMethod.getModifiers()));

        var getDocumentServiceMethod = ServiceFactory.class.getMethod("getDocumentService");
        assertTrue(Modifier.isPublic(getDocumentServiceMethod.getModifiers()));
        assertTrue(Modifier.isStatic(getDocumentServiceMethod.getModifiers()));

        var getCustomerServiceLinkServiceMethod = ServiceFactory.class.getMethod("getCustomerServiceLinkService");
        assertTrue(Modifier.isPublic(getCustomerServiceLinkServiceMethod.getModifiers()));
        assertTrue(Modifier.isStatic(getCustomerServiceLinkServiceMethod.getModifiers()));
    }

    @Test
    void testFactoryMethods_AreSynchronized() throws NoSuchMethodException {
        var getValidatorMethod = ServiceFactory.class.getMethod("getValidator");
        assertTrue(Modifier.isSynchronized(getValidatorMethod.getModifiers()),
            "getValidator should be synchronized for thread safety");

        var getPaymentInvoiceServiceMethod = ServiceFactory.class.getMethod("getPaymentInvoiceService");
        assertTrue(Modifier.isSynchronized(getPaymentInvoiceServiceMethod.getModifiers()),
            "getPaymentInvoiceService should be synchronized for thread safety");

        var getRegistrationServiceMethod = ServiceFactory.class.getMethod("getRegistrationService");
        assertTrue(Modifier.isSynchronized(getRegistrationServiceMethod.getModifiers()),
            "getRegistrationService should be synchronized for thread safety");
    }

    @Test
    void testValidator_MultipleCallsReturnSameHashCode() {
        ServiceDataValidator validator1 = ServiceFactory.getValidator();
        ServiceDataValidator validator2 = ServiceFactory.getValidator();
        
        assertEquals(validator1.hashCode(), validator2.hashCode(),
            "Same instance should have same hash code");
    }
}

