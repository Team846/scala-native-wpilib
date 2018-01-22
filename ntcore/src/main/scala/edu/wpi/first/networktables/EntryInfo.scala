/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

/**
  * NetworkTables Entry information.
  *
  * Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param inst  Instance
  * @param entry Entry handle
  * @param name  Name
  * @param type  Type (integer version of { @link NetworkTableType})
  * @param flags       Flags
  * @param last_change Timestamp of last change
  */
final class EntryInfo(/* Network table instance. */ val inst: NetworkTableInstance,

                      /** Entry handle. */
                      val entry: Int,

                      /** Entry name. */
                      val name: String, val _tpe: Int,

                      /** Entry flags. */
                      val flags: Int,

                      /** Timestamp of last change to entry (type or value). */
                      val last_change: Long) {
  /** Entry type. */
  final var `type`: NetworkTableType = NetworkTableType.getFromInt(_tpe)
  /* Cached entry object. */ private var entryObject: NetworkTableEntry = null

  /**
    * Get the entry as an object.
    *
    * @return NetworkTableEntry for this entry.
    */
  private[networktables] def getEntry = {
    if (entryObject == null) entryObject = new NetworkTableEntry(inst, entry)
    entryObject
  }
}
