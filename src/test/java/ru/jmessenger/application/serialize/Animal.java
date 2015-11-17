package ru.jmessenger.application.serialize;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created by ������� on 16.10.2015.
 */
class Animal implements Serializable{
    private final String name;

    public Animal(String name) {
        this.name = name;
    }

    public static Animal[] deserializeAnimalArray(byte[] data) {
        // your impelementation here
        Animal[] animals;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            int size = ois.readInt();
            animals = new Animal[size];
            for (int i = 0; i < size; i++) {
                animals[i] = (Animal) ois.readObject();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        return animals;
    }

    public static byte[] serializeAnumalArray(Animal[] animals) throws Exception{
        int size = animals.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeInt(size);
            for (Animal animal : animals) {
                oos.writeObject(animal);
            }
        }
        return baos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        Animal[] animals = new Animal[] {new Animal("Dog"), new Animal("Lion"), new Animal("Camel"), new Animal("Jeraffe")};
        byte[] data = serializeAnumalArray(animals);
        Animal[] deserializedAnimals = deserializeAnimalArray(data);
        for (Animal animal : deserializedAnimals) {
            System.out.println(animal.toString());
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Animal) {
            return Objects.equals(name, ((Animal) obj).name);
        }
        return false;
    }
}
