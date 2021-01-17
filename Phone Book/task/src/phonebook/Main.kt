package phonebook

import java.io.File
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {

    val findFilePath = "/Users/caique/Documents/Phone Book/Phone Book/task/src/phonebook/find.txt"
    val directoryFilePath = "/Users/caique/Documents/Phone Book/Phone Book/task/src/phonebook/directory.txt"
    val findFile = File(findFilePath)
    val directoryFile = File(directoryFilePath)

    var foundJump = 0
    var foundBinary = 0
    var foundHash = 0
    val contactList = ContactList()
    val hashtable = hashMapOf<Int, String>()
    val contactsDir = mutableListOf<String>()
    val findLines = findFile.readLines()
    val findEntries = findLines.size
    directoryFile.forEachLine {

        val element = it.split(Regex("(?<=\\d) (?=\\D)"))
        contactsDir.add(element[1])
        contactList.addContact(Contact(element))
    }

    println("Start searching (linear search)...")
    val timeBegin = System.currentTimeMillis()
    val foundLinear = contactList.linearSearchList(findLines)

    printFounds(foundLinear, findEntries, System.currentTimeMillis() - timeBegin)

    println("Start searching (bubble sort + jump search)...")

    val timeSearch: Long
    val timeSorting = System.currentTimeMillis()
    if (!contactList.bubbleSortList(System.currentTimeMillis() - timeBegin)) {

        timeSearch = System.currentTimeMillis()
        foundJump = contactList.linearSearchList(findLines)
    } else {

        timeSearch = System.currentTimeMillis()
        findLines.forEach {
            foundJump = contactList.jumpSearchList(it)
        }
    }

    val timeJump = System.currentTimeMillis()

    printFounds(foundJump, findEntries, timeJump - timeSorting)
    printSorting(timeSearch - timeSorting)
    printSearching(timeJump - timeSearch)


    println("Start searching (quick sort + binary search)...")
    val timeQuick = System.currentTimeMillis()
    quicksort(contactsDir)

    val timeBinarySearch = System.currentTimeMillis()
    findLines.forEach {
        if (contactsDir.binarySearch(it) >= 0) foundBinary++
    }

    val endTime = System.currentTimeMillis()

    printFounds(foundBinary, findEntries, endTime - timeQuick)
    printSorting(timeBinarySearch - timeQuick)
    printSearching(endTime - timeBinarySearch)

    println("Start searching (hash table)...")

    val startTimeHashtable = System.currentTimeMillis()
    contactsDir.forEach {
        hashtable[it.hashCode()] = it
    }
    val stopTimeHashtable = System.currentTimeMillis()

    findLines.forEach { if (hashtable.contains(it.hashCode())) foundHash++ }

    val stopTimeSearchHashtable = System.currentTimeMillis()

    printFounds(foundHash, findEntries, stopTimeSearchHashtable - startTimeHashtable)

    println("Creating time: " +
                String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", stopTimeHashtable - startTimeHashtable))

    printSearching(stopTimeSearchHashtable - stopTimeHashtable)

}

class Contact(private val number: Int, val name: String) {

    constructor(number_name: List<String>) : this(number_name[0].toInt(), number_name[1])

    override fun toString(): String {
        return "Name: $name, Number: $number"

    }
}

class ContactList {

    private val contacts: MutableList<Contact> = ArrayList()
    private var foundJumpSearch = 0

    fun addContact(contact: Contact) {

        contacts.add(contact)
    }

    fun bubbleSortList(maxTime: Long): Boolean {

        var swap = true
        var aux: Contact

        val startTime = System.currentTimeMillis()
        while (swap) {

            if ((System.currentTimeMillis() - startTime) > maxTime * 10) {

                return false
            }
            swap = false
            for (i in 0 until contacts.size - 1) {

                if (contacts[i].name > contacts[i + 1].name) {

                    aux = contacts[i]
                    contacts[i] = contacts[i + 1]
                    contacts[i + 1] = aux
                    swap = true
                }
            }
        }
        return true
    }

    fun linearSearchList(list: List<String>): Int {

        var found = 0
        for (line in list) {

            for (contact in contacts) {
                if (contact.name == line) found++
            }
        }

        return found
    }

    fun jumpSearchList(name: String): Int {

        val blockSize = floor(sqrt(contacts.size.toDouble())).toInt()

        if (contacts[0].name == name) {

            foundJumpSearch++
            return foundJumpSearch
        }
        for (i in contacts.indices step blockSize) {

            if (contacts[i].name == name) {

                return 1
            } else if (contacts[i].name < name && i != 0) {

                for (j in i downTo i - blockSize) {

                    if (contacts[j].name == name) {

                        foundJumpSearch++
                    }

                }
            } else {

                return 0
            }
        }
        return foundJumpSearch
    }
}

fun partition(array: MutableList<String>, startIndex: Int, endIndex: Int): Int {
    var start = startIndex
    var end = endIndex

    while (start < end) {
        while (start < end) {
            if (array[start] > array[end]) {
                val swap = array[start]
                array[start] = array[end]
                array[end] = swap
                break
            }
            end -= 1
        }
        while (start < end) {
            if (array[start] > array[end]) {
                val swap = array[start]
                array[start] = array[end]
                array[end] = swap
                break
            }
            start += 1
        }
    }
    return start
}

fun quicksort(array: MutableList<String>, startIndex: Int = -1, endIndex: Int = -1) {
    var start = startIndex
    var end = endIndex

    if (start == -1) start = 0
    if (end == -1) end = array.size

    if (start < end) {
        val i = partition(array, start, end - 1)
        quicksort(array, start, i)
        quicksort(array, i + 1, end)
    }
}

fun printFounds(founds: Int, findEntries: Int, time: Long) {

    println("Found $founds / $findEntries entries. Time taken: " +
                String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.\n", time))
}

fun printSorting(time: Long) {

    println("Sorting time: " +
                String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", time))
}

fun printSearching(time: Long) {

    println("Searching time: " +
                String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.\n", time))
}