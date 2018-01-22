/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

/**
  * NetworkTables Entry notification.
  *
  * Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param inst     Instance
  * @param listener Listener that was triggered
  * @param entry    Entry handle
  * @param name     Entry name
  * @param value    The new value
  * @param flags    Update flags
  */
final class EntryNotification(/* Network table instance. */ val inst: NetworkTableInstance,

                              /**
                                * Listener that was triggered.
                                */
                              val listener: Int,

                              /**
                                * Entry handle.
                                */
                              val entry: Int,

                              /**
                                * Entry name.
                                */
                              val name: String,

                              /**
                                * The new value.
                                */
                              val value: Nothing,

                              /**
                                * Update flags.  For example, {@link EntryListenerFlags#kNew} if the key did
                                * not previously exist.
                                */
                              val flags: Int) {
  /* Cached entry object. */ private[networktables] var entryObject: NetworkTableEntry = null

  /**
    * Get the entry as an object.
    *
    * @return NetworkTableEntry for this entry.
    */
  def getEntry: NetworkTableEntry = {
    if (entryObject == null) entryObject = new NetworkTableEntry(inst, entry)
    entryObject
  }
}
