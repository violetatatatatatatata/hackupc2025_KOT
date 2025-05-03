package cat.hackupc.signalchain.repository

import cat.hackupc.signalchain.model.Person

object PersonRepository {
    val people = mutableListOf<Person>()

    fun merge(newPeople: List<Person>) {
        newPeople.forEach { incoming ->
            val exists = people.any {
                it.firstName.equals(incoming.firstName, true) &&
                        it.lastName.equals(incoming.lastName, true)
            }
            if (!exists) {
                people.add(incoming)
            }
        }
    }
}
