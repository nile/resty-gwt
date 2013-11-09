/**
 * Copyright (C) 2009-2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.restygwt.client.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.fusesource.restygwt.client.AbstractJsonEncoderDecoder;
import org.fusesource.restygwt.client.AbstractNestedJsonEncoderDecoder;
import org.fusesource.restygwt.client.Json;
import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.client.ObjectEncoderDecoder;
import org.fusesource.restygwt.client.codec.EncoderDecoderTestGwt.WithEnum.Cycle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class EncoderDecoderTestGwt extends GWTTestCase {

    public interface WrapperLibraryCodec extends JsonEncoderDecoder<LibraryWithWrapper> {
    }

    public interface ArrayWrapperLibraryCodec extends JsonEncoderDecoder<LibraryWithArrayWrapper> {
    }

    public interface PropertyLibraryCodec extends JsonEncoderDecoder<LibraryWithProperty> {
    }

    static class ANumber<T extends Number> {

        T n;

        T get() {
            return n;
        }
    }

    public interface IntegerCodec extends JsonEncoderDecoder<ANumber<Integer>> {
    }

    public interface FloatCodec extends JsonEncoderDecoder<ANumber<Float>> {
    }
    
    public static class Name {
        public String name;
    }

    public static class Foo {
        public List<String> bars = new ArrayList<String>();
        public List<Name> names = new ArrayList<Name>();
    }

    public interface FooCodec extends JsonEncoderDecoder<Foo> {
    }

    @Override
    public String getModuleName() {
        return "org.fusesource.restygwt.EncoderDecoderTestGwt";
    }

    public void testNullPrimitiveValueInList() {
        FooCodec fooCoder = GWT.create(FooCodec.class);

        Foo foo = new Foo();
        foo.bars.add(null);
        JSONValue fooJ = fooCoder.encode(foo);
        assertEquals(foo.bars, fooCoder.decode(fooJ).bars);
    }

    public void testNullPojoValueInList() {
        FooCodec fooCoder = GWT.create(FooCodec.class);

        Foo foo = new Foo();
        foo.names.add(null);
        JSONValue fooJ = fooCoder.encode(foo);
        assertEquals(foo.names, fooCoder.decode(fooJ).names);
    }

    public void testSubtypeWrappeObjectWithSingleSubtype() {
        WrapperLibraryCodec lc = GWT.create(WrapperLibraryCodec.class);
        LibraryWithWrapper l = new LibraryWithWrapper();
        ArrayList<LibraryItemWithWrapper> libraryItems = new ArrayList<LibraryItemWithWrapper>();
        SpriteBasedItemWithWrapper li = new SpriteBasedItemWithWrapper();
        li.id = "1";
        li.imageRef = "src.png";
        libraryItems.add(li);
        l.items = libraryItems;

        JSONValue encode = lc.encode(l);
        LibraryWithWrapper decode = lc.decode(encode);
        assertEquals(l, decode);
    }

    public void testSubtypeArrayWrappeObjectWithSingleSubtype() {
        ArrayWrapperLibraryCodec lc = GWT.create(ArrayWrapperLibraryCodec.class);
        LibraryWithArrayWrapper l = new LibraryWithArrayWrapper();
        ArrayList<LibraryItemWithArrayWrapper> libraryItems = new ArrayList<LibraryItemWithArrayWrapper>();
        SpriteBasedItemWithArrayWrapper li = new SpriteBasedItemWithArrayWrapper();
        li.id = "1";
        li.imageRef = "src.png";
        libraryItems.add(li);
        l.items = libraryItems;

        JSONValue encode = lc.encode(l);
        LibraryWithArrayWrapper decode = lc.decode(encode);
        assertEquals(l, decode);
    }

    public void testSubtypePropertytWithSingleSubtype() {
        PropertyLibraryCodec lc = GWT.create(PropertyLibraryCodec.class);
        LibraryWithProperty l = new LibraryWithProperty();
        ArrayList<LibraryItemWithProperty> libraryItems = new ArrayList<LibraryItemWithProperty>();
        SpriteBasedItemWithProperty li = new SpriteBasedItemWithProperty();
        li.id = "1";
        li.imageRef = "src.png";
        libraryItems.add(li);
        l.items = libraryItems;

        JSONValue encode = lc.encode(l);
        LibraryWithProperty decode = lc.decode(encode);
        assertEquals(l, decode);
    }

    public void testGenericTypes() {
        IntegerCodec integerCoder = GWT.create(IntegerCodec.class);
        FloatCodec floatCoder = GWT.create(FloatCodec.class);

        ANumber<Integer> intA = new ANumber<Integer>();
        intA.n = 123;
        JSONValue intJ = integerCoder.encode(intA);

        assertEquals(intA.n, integerCoder.decode(intJ).n);

        ANumber<Float> floatA = new ANumber<Float>();
        floatA.n = 123.456f;
        JSONValue floatJ = floatCoder.encode(floatA);

        assertEquals(floatA.n, floatCoder.decode(floatJ).n);
    }

    public interface CreatorCodec extends JsonEncoderDecoder<CredentialsWithCreator> {
    }

    public void testCreators() {
        CreatorCodec codec = GWT.create(CreatorCodec.class);
        CredentialsWithCreator c = new CredentialsWithCreator("email", "password");
        c.age = 12;
        JSONValue cJson = codec.encode(c);
        CredentialsWithCreator cRoundTrip = codec.decode(cJson);
        assertEquals("email", cRoundTrip.email);
        assertEquals("password", cRoundTrip.password);
        assertEquals(12, cRoundTrip.age);
    }

    public void testCreatorsWithNullValue() {
        CreatorCodec codec = GWT.create(CreatorCodec.class);
        CredentialsWithCreator c = new CredentialsWithCreator(null, "password");
        c.age = 12;
        JSONValue cJson = codec.encode(c);
        CredentialsWithCreator cRoundTrip = codec.decode(cJson);
        assertNull(cRoundTrip.email);
        assertEquals("password", cRoundTrip.password);
        assertEquals(12, cRoundTrip.age);
    }

    public interface WrapperCodec extends JsonEncoderDecoder<CredentialsWithWrapperObject> {
    }

    public interface SubWrapperCodec extends JsonEncoderDecoder<SubCredentialsWithWrapperObject> {
    }

    public void testSubtypeWrapperObject() {
        WrapperCodec codec = GWT.create(WrapperCodec.class);
        CredentialsWithWrapperObject base = new CredentialsWithWrapperObject();
        base.setEmail("email-super");
        base.setPassword("password-super");
        JSONValue baseJson = codec.encode(base);
        CredentialsWithWrapperObject baseRoundTrip = codec.decode(baseJson);
        assertEquals("email-super", baseRoundTrip.email);
        assertEquals("password-super", baseRoundTrip.password);
        assertFalse(baseRoundTrip.getClass().equals(SubCredentialsWithWrapperObject.class));

        SubCredentialsWithWrapperObject sub = new SubCredentialsWithWrapperObject();
        sub.setEmail("email-sub");
        sub.setPassword("password-sub");
        sub.login = "login-sub";
        JSONValue subJson = codec.encode(sub);
        SubCredentialsWithWrapperObject subRoundTrip = (SubCredentialsWithWrapperObject) codec.decode(subJson);
        assertEquals("email-sub", subRoundTrip.email);
        assertEquals("password-sub", subRoundTrip.password);
        assertEquals("login-sub", subRoundTrip.login);

        SubWrapperCodec subCodec = GWT.create(SubWrapperCodec.class);
        sub.setEmail("email-direct");
        sub.setPassword("password-direct");
        sub.login = "login-direct";
        subJson = subCodec.encode(sub);
        subRoundTrip = subCodec.decode(subJson);
        assertEquals("email-direct", subRoundTrip.email);
        assertEquals("password-direct", subRoundTrip.password);
        assertEquals("login-direct", subRoundTrip.login);
    }

    public interface PropertyCodec extends JsonEncoderDecoder<CredentialsWithProperty> {
    }

    public interface SubPropertyCodec extends JsonEncoderDecoder<SubCredentialsWithProperty> {
    }

    public void testSubtypeProperty() {
        PropertyCodec codec = GWT.create(PropertyCodec.class);
        CredentialsWithProperty base = new CredentialsWithProperty();
        base.setEmail("email-super");
        base.setPassword("password-super");
        JSONValue baseJson = codec.encode(base);
        CredentialsWithProperty baseRoundTrip = codec.decode(baseJson);
        assertEquals("email-super", baseRoundTrip.email);
        assertEquals("password-super", baseRoundTrip.password);
        assertFalse(baseRoundTrip.getClass().equals(SubCredentialsWithProperty.class));

        SubCredentialsWithProperty sub = new SubCredentialsWithProperty();
        sub.setEmail("email-sub");
        sub.setPassword("password-sub");
        sub.login = "login-sub";
        JSONValue subJson = codec.encode(sub);
        SubCredentialsWithProperty subRoundTrip = (SubCredentialsWithProperty) codec.decode(subJson);
        assertEquals("email-sub", subRoundTrip.email);
        assertEquals("password-sub", subRoundTrip.password);
        assertEquals("login-sub", subRoundTrip.login);

        SubPropertyCodec subCodec = GWT.create(SubPropertyCodec.class);
        sub.setEmail("email-direct");
        sub.setPassword("password-direct");
        sub.login = "login-direct";
        subJson = subCodec.encode(sub);
        subRoundTrip = subCodec.decode(subJson);
        assertEquals("email-direct", subRoundTrip.email);
        assertEquals("password-direct", subRoundTrip.password);
        assertEquals("login-direct", subRoundTrip.login);
    }

    static class B {
        BigInteger age;
    }

    static interface BigCodec extends JsonEncoderDecoder<B> {
    }

    public void testBigIntegers() {
        BigCodec big = GWT.create(BigCodec.class);
        B b = new B();
        b.age = new BigInteger("1234567890123456789012345678901234567890");
        JSONValue bJson = big.encode(b);
        B bRoundTrip = big.decode(bJson);
        assertEquals(b.age, bRoundTrip.age);
    }

    public void testObjectEncoderDecoder() {
        {
            double value = Math.random() * 10000;
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            String value = "Fred Flintstone";
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            boolean value = Boolean.TRUE;
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("fred", "flintstone");
            value.put("shoeSize", 12.0);
            value.put("geek", true);
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }
        {
            List<Object> value = new ArrayList<Object>();
            value.add("Fred Flintstone");
            value.add(12.0);
            value.add(false);
            JSONValue json = ObjectEncoderDecoder.INSTANCE.encode(value);
            assertEquals(value, ObjectEncoderDecoder.INSTANCE.decode(json));
        }

    }

    public void testIntegerToStringDecode() {
        Integer i = 123;
        assertEquals(i.toString(), AbstractJsonEncoderDecoder.STRING.decode(AbstractJsonEncoderDecoder.INT.encode(i)));
    }

    public void testBooleanToStringDecode() {
        Boolean b = true;
        assertEquals(b.toString(),
                AbstractJsonEncoderDecoder.STRING.decode(AbstractJsonEncoderDecoder.BOOLEAN.encode(b)));
    }
    
    public void testBooleanArrayDecode() {
        boolean[] array = {true, false};
        AbstractJsonEncoderDecoder<Boolean> encoder = AbstractJsonEncoderDecoder.BOOLEAN;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new boolean[2])));
    }

    public void testByteArrayDecode() {
        byte[] array = {2, 8};
        AbstractJsonEncoderDecoder<Byte> encoder = AbstractJsonEncoderDecoder.BYTE;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new byte[2])));
    }

    public void testCharacterArrayDecode() {
        char[] array = {'a','z'};
        AbstractJsonEncoderDecoder<Character> encoder = AbstractJsonEncoderDecoder.CHAR;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new char[2])));
    }

    public void testFloatArrayDecode() {
        float[] array = {1.4e19f, -13.53e-18f};
        AbstractJsonEncoderDecoder<Float> encoder = AbstractJsonEncoderDecoder.FLOAT;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new float[2])));
    }

    public void testDoubleArrayDecode() {
        double[] array = {1.4e193, -13.53e-188};
        AbstractJsonEncoderDecoder<Double> encoder = AbstractJsonEncoderDecoder.DOUBLE;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new double[2])));
    }

    public void testShortArrayDecode() {
        short[] array = {1, -13};
        AbstractJsonEncoderDecoder<Short> encoder = AbstractJsonEncoderDecoder.SHORT;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new short[2])));
    }

    public void testIntArrayDecode() {
        int[] array = {1010, -13100};
        AbstractJsonEncoderDecoder<Integer> encoder = AbstractJsonEncoderDecoder.INT;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new int[2])));
    }

    public void testLongArrayDecode() {
        long[] array = {1010, -13100};
        AbstractJsonEncoderDecoder<Long> encoder = AbstractJsonEncoderDecoder.LONG;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new long[2])));
    }

    public void testTypeArrayDecode() {
        String[] array = {"may", "all", "be", "happy"};
        AbstractJsonEncoderDecoder<String> encoder = AbstractJsonEncoderDecoder.STRING;
        assertEquals(Arrays.toString(array),
                Arrays.toString(AbstractJsonEncoderDecoder.toArray(AbstractJsonEncoderDecoder.toJSON(array, encoder), 
                            encoder, new String[4])));
    }

    public void testTypeMapDecode() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("123", 321);
        AbstractJsonEncoderDecoder<Integer> encoder = AbstractJsonEncoderDecoder.INT;
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, encoder, Json.Style.DEFAULT), 
                        encoder, 
                        Json.Style.DEFAULT).toString());
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, encoder, Json.Style.JETTISON_NATURAL), 
                        encoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }

    public void testTypeMapWithIntegerDecode() {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(123, "321");
        AbstractJsonEncoderDecoder<Integer> keyEncoder = AbstractJsonEncoderDecoder.INT;
        AbstractJsonEncoderDecoder<String> valueEncoder = AbstractJsonEncoderDecoder.STRING;
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, valueEncoder, Json.Style.DEFAULT), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.DEFAULT).toString());
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, valueEncoder, Json.Style.JETTISON_NATURAL), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }

    public void testTypeMapWithBigDecimalDecode() {
        Map<BigDecimal, String> map = new HashMap<BigDecimal, String>();
        map.put(BigDecimal.valueOf(123), "321");
        AbstractJsonEncoderDecoder<BigDecimal> keyEncoder = AbstractJsonEncoderDecoder.BIG_DECIMAL;
        AbstractJsonEncoderDecoder<String> valueEncoder = AbstractJsonEncoderDecoder.STRING;
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, valueEncoder, Json.Style.DEFAULT), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.DEFAULT).toString());
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, valueEncoder, Json.Style.JETTISON_NATURAL), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }

    static class Email {
        String name, email;
        public String toString(){
            return name + "<" + email + ">";
        }
    }

    static interface EmailCodec extends JsonEncoderDecoder<Email> {
    }
        
    public void testTypeMapWithComplexDecode() {
        Map<Email, String> map = new HashMap<Email, String>();
        Email email = new Email();
        email.email = "me@example.com";
        email.name = "me";
        map.put(email, "done");
        AbstractJsonEncoderDecoder<Email> keyEncoder = GWT.create(EmailCodec.class);
        AbstractJsonEncoderDecoder<String> valueEncoder = AbstractJsonEncoderDecoder.STRING;

        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, valueEncoder, Json.Style.DEFAULT), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.DEFAULT).toString());
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, valueEncoder, Json.Style.JETTISON_NATURAL), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }

    public void testTypeMapWithListValueDecode() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("key", new ArrayList<String>(Arrays.asList("me and the corner")));
        AbstractJsonEncoderDecoder<List<String>> valueEncoder =
                AbstractNestedJsonEncoderDecoder.listEncoderDecoder( AbstractJsonEncoderDecoder.STRING );
        
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, valueEncoder, Json.Style.DEFAULT), 
                        valueEncoder, 
                        Json.Style.DEFAULT).toString());
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, valueEncoder, Json.Style.JETTISON_NATURAL), 
                        valueEncoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }

    public void testTypeMapWithMapValueDecode() {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<String, String> nested = new HashMap<String, String>();
        nested.put("name", "me");
        map.put("key", nested);

        AbstractJsonEncoderDecoder<Map<String, String>> valueEncoder =
                AbstractNestedJsonEncoderDecoder.mapEncoderDecoder( AbstractJsonEncoderDecoder.STRING,
                                                                    AbstractJsonEncoderDecoder.STRING,
                                                                    Json.Style.DEFAULT );
        
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, valueEncoder, Json.Style.DEFAULT), 
                        valueEncoder, 
                        Json.Style.DEFAULT).toString());
        
        // the JETTISON enoding it is important to use the special encoder with String keys
        valueEncoder = AbstractNestedJsonEncoderDecoder.mapEncoderDecoder( AbstractJsonEncoderDecoder.STRING, Json.Style.JETTISON_NATURAL );
        
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, valueEncoder, Json.Style.JETTISON_NATURAL), 
                        valueEncoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }
    
    public void testTypeMapWithListValueDecodeAndComplexKey() {
        Map<Email, List<String>> map = new HashMap<Email, List<String>>();
        Email email = new Email();
        email.email = "me@example.com";
        email.name = "me";
        map.put(email, new ArrayList<String>(Arrays.asList("me and the corner")));
        AbstractJsonEncoderDecoder<Email> keyEncoder = GWT.create(EmailCodec.class);
        AbstractJsonEncoderDecoder<List<String>> valueEncoder = AbstractNestedJsonEncoderDecoder.listEncoderDecoder( AbstractJsonEncoderDecoder.STRING );
  
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, 
                        valueEncoder, Json.Style.DEFAULT), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.DEFAULT).toString());
        assertEquals(map.toString(),
                AbstractJsonEncoderDecoder.toMap(AbstractJsonEncoderDecoder.toJSON(map, keyEncoder, 
                        valueEncoder, Json.Style.JETTISON_NATURAL), 
                        keyEncoder, 
                        valueEncoder, 
                        Json.Style.JETTISON_NATURAL).toString());
    }

    static interface WithArraysAndCollectionsCodec extends JsonEncoderDecoder<WithArraysAndCollections> {}
    
    @SuppressWarnings("unchecked")
    public void testTypeWithArrasAndCollections() {
        WithArraysAndCollections obj = new WithArraysAndCollections();
        
        obj.ages = new int[] { 1, 2, 3, 4 };

        obj.ageSet = new HashSet<int[]>();
        obj.ageSet.add( obj.ages );
        
        Email email = new Email();
        email.email = "me@example.com";
        email.name = "me";
        
        obj.emailArray = new Email[]{ email };
        
        obj.emailList = new ArrayList<Email>();
        obj.emailList.add(email);

        obj.emailSet = new HashSet<Email>();
        obj.emailSet.add( email );
        
        obj.emailListArray = new List[ 1 ];
        obj.emailListArray[ 0 ] = obj.emailList;
        
        obj.emailSetArray = new Set[ 1 ];
        obj.emailSetArray[ 0 ] = obj.emailSet;
        
        obj.personalEmailList = new HashMap<String, List<Email>>();
        obj.personalEmailList.put( "me", obj.emailList );

        obj.personalEmailSet = new HashMap<String, Set<Email>>();
        obj.personalEmailSet.put( "me", obj.emailSet );

        obj.personalEmailListArray = new HashMap<String, List<Email>[]>();
        obj.personalEmailListArray.put( "me", obj.emailListArray );

        obj.personalEmailSetArray = new HashMap<String, Set<Email>[]>();
        obj.personalEmailSetArray.put( "me", obj.emailSetArray );

        obj.personalEmailSetList = new ArrayList<Map<String, Set<Email>>>();
        obj.personalEmailSetList.add( obj.personalEmailSet );

        obj.personalEmailListSet = new HashSet<Map<String, List<Email>>>();
        obj.personalEmailListSet.add( obj.personalEmailList );

        obj.personalEmailSetMap = new HashMap<Email, Map<String, Set<Email>>>();
        obj.personalEmailSetMap.put( email, obj.personalEmailSet );
        
        AbstractJsonEncoderDecoder<WithArraysAndCollections> encoder = GWT.create(WithArraysAndCollectionsCodec.class);
  
        JSONValue json = encoder.encode(obj);
        assertEquals("{\"ages\":[1,2,3,4], " +
                "\"emailArray\":[{\"name\":\"me\", \"email\":\"me@example.com\"}], " +
                "\"emailList\":[{\"name\":\"me\", \"email\":\"me@example.com\"}], " +
                "\"emailSet\":[{\"name\":\"me\", \"email\":\"me@example.com\"}], " +
        		"\"emailListArray\":[[{\"name\":\"me\", \"email\":\"me@example.com\"}]], " +
                "\"emailSetArray\":[[{\"name\":\"me\", \"email\":\"me@example.com\"}]], " +
        		"\"personalEmailList\":{\"me\":[{\"name\":\"me\", \"email\":\"me@example.com\"}]}, " +
        		"\"personalEmailSet\":{\"me\":[{\"name\":\"me\", \"email\":\"me@example.com\"}]}" +
        		", \"personalEmailListArray\":{\"me\":[[{\"name\":\"me\", \"email\":\"me@example.com\"}]]}" +
        		", \"personalEmailSetArray\":{\"me\":[[{\"name\":\"me\", \"email\":\"me@example.com\"}]]}" +
                ", \"personalEmailSetList\":[{\"me\":[{\"name\":\"me\", \"email\":\"me@example.com\"}]}]" +
                ", \"personalEmailListSet\":[{\"me\":[{\"name\":\"me\", \"email\":\"me@example.com\"}]}]" + 
                ", \"personalEmailSetMap\":{\"{\\\"name\\\":\\\"me\\\", \\\"email\\\":\\\"me@example.com\\\"}\":" +
                "{\"me\":[{\"name\":\"me\", \"email\":\"me@example.com\"}]}}" +
        		"}",
                json.toString() );

        WithArraysAndCollections roundtrip = encoder.decode(json);
        assertEquals("[1, 2, 3, 4],[me<me@example.com>],[me<me@example.com>],{me=[me<me@example.com>]},null," +
        		"[me<me@example.com>],{me=[me<me@example.com>]},[[me<me@example.com>]],[[me<me@example.com>]]," +
        		"[me]=>[[me<me@example.com>]],[me]=>[[me<me@example.com>]],[{me=[me<me@example.com>]}],[{me=[me<me@example.com>]}]," +
        		"{me<me@example.com>={me=[me<me@example.com>]}}",
                roundtrip.toString());
    }

    static class CCC {
        
        @JsonIgnore
        private String lastName;
        
        String name;
        
        @JsonIgnore
        String firstName;
        
        String getLastName(){
            return lastName;
        }
        
        void setLastName(String name){
            lastName = name;
        }
    }

    static interface CCCCodec extends JsonEncoderDecoder<CCC> {
    }

    public void testIgnores() {
        CCCCodec cccc = GWT.create(CCCCodec.class);
        CCC ccc = new CCC();
        ccc.name = "me and the corner";
        ccc.firstName = "chaos";
        
        JSONValue json = cccc.encode(ccc);
        assertEquals("{\"name\":\"me and the corner\"}", json.toString());
        CCC roundTrip = cccc.decode(json);
        assertEquals(ccc.name, roundTrip.name);
        assertNull(roundTrip.firstName);
        assertNull(roundTrip.getLastName());
    }
    
    static class Shorty {

        private short shorty;
        private long id;

        public Shorty() {
            shorty = 0;
        }

        public short getShorty() {
            return shorty;
        }

        public void setShorty(short shorty) {
            this.shorty = shorty;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

    }
    
    static interface ShortyCodec extends JsonEncoderDecoder<Shorty> {
    }
    
    public void testShortys() {
        ShortyCodec shortyCodec = GWT.create(ShortyCodec.class);
        Shorty shorty = new Shorty();
        
        JSONValue json = shortyCodec.encode(shorty);
        assertEquals("{\"shorty\":0, \"id\":0}", json.toString());
        Shorty roundTrip = shortyCodec.decode(json);
        assertEquals(shorty.getShorty(), 0);
        assertEquals(roundTrip.getShorty(), 0);
    }
    
    static class Bean {
        
        @JsonIgnore
        private int myAge = 123;

        int getAge(){
            return myAge;
        }
        
        void setAge( int a ){
            this.myAge = a;
        }
        
        void setYearOfBirth( long a ){
        }
        
        long getYearOfBirth(){
            return 1234;
        }
    }

    static class NestedBean extends Bean {

        String name = "asterix";

        String getName(){
            return name;
        }
        
        void setName( String name ){
            this.name = name;
        }
    }
    
    static interface BeanCodec extends JsonEncoderDecoder<Bean> {
    }
    static interface NestedBeanCodec extends JsonEncoderDecoder<NestedBean> {
    }
    
    public void testBean() {
        BeanCodec beanCodec = GWT.create(BeanCodec.class);
        Bean bean = new Bean();
        
        JSONValue json = beanCodec.encode(bean);
        assertEquals("{\"age\":123, \"yearOfBirth\":1234}", json.toString());
        Bean roundTrip = beanCodec.decode(json);
        assertEquals(roundTrip.getAge(), 123);
        assertEquals(roundTrip.getYearOfBirth(), 1234);
    }
    

    public void testNestedBean() {
        NestedBeanCodec beanCodec = GWT.create(NestedBeanCodec.class);
        NestedBean bean = new NestedBean();
        
        JSONValue json = beanCodec.encode(bean);
        assertEquals("{\"name\":\"asterix\", \"age\":123, \"yearOfBirth\":1234}", json.toString());
        NestedBean roundTrip = beanCodec.decode(json);
        assertEquals(roundTrip.getAge(), 123);
        assertEquals(roundTrip.getYearOfBirth(), 1234);
    }

    static class Renamed {
        
        @JsonProperty( "my-age")
        private int age;
        
        @Json( name = "year-of-birth")
        private long yearOfBirth;

        private String n;
        
        int getAge(){
            return age;
        }
        
        void setAge( int a ){
            this.age = a;
        }
        
        void setYearOfBirth( long a ){
            this.yearOfBirth = a;
        }
        
        long getYearOfBirth(){
            return yearOfBirth;
        }
        
        void setName( String name ){
            this.n = name;
        }

        @JsonProperty( "my-name")
        String getName(){
            return n;
        }
    }
    
    static interface RenamedCodec extends JsonEncoderDecoder<Renamed> {
    }
    
    public void testRenamed() {
        RenamedCodec renamedCodec = GWT.create(RenamedCodec.class);
        Renamed renamed = new Renamed();
        renamed.setName("marvin the robot");
        renamed.setAge(123);
        renamed.setYearOfBirth(1234);
        
        JSONValue json = renamedCodec.encode(renamed);
        assertEquals("{\"my-age\":123, \"year-of-birth\":1234, \"my-name\":\"marvin the robot\"}", json.toString());
        Renamed roundTrip = renamedCodec.decode(json);
        assertEquals(roundTrip.age, 123);
        assertEquals(roundTrip.yearOfBirth, 1234);
        assertEquals(roundTrip.getName(), "marvin the robot");
    }

    static class WithEnum {
        
        enum Cycle { BEGIN, LIFE, END } 
      
        public Cycle first;
        
        private Cycle last;

        public Cycle getLast() {
            return last;
        }

        public void setLast(Cycle last) {
            this.last = last;
        }
        
    }

    static interface WithEnumCodec extends JsonEncoderDecoder<WithEnum> {
    }

    public void testWithEnum() {
        WithEnumCodec codec = GWT.create(WithEnumCodec.class);
        WithEnum pojo = new WithEnum();
        pojo.first = WithEnum.Cycle.BEGIN;
        pojo.setLast( WithEnum.Cycle.END );
    
        JSONValue json = codec.encode( pojo );
        assertEquals("{\"first\":\"BEGIN\", \"last\":\"END\"}", json.toString());
        WithEnum roundTrip = codec.decode( json );
        assertEquals( roundTrip.first, Cycle.BEGIN );
        assertEquals( roundTrip.getLast(), Cycle.END );
        
        pojo.first = null;
        pojo.setLast( null );
    
        json = codec.encode( pojo );
        assertEquals("{\"first\":null, \"last\":null}", json.toString());
        roundTrip = codec.decode( json );
        assertEquals( roundTrip.first, null );
        assertEquals( roundTrip.getLast(), null );
    }
}
