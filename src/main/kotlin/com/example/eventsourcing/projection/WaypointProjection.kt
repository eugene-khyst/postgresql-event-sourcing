package com.example.eventsourcing.projection

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class WaypointProjection : Serializable {
    var address: String? = null

    @set:JsonProperty("lat")
    @JsonProperty("lat")
    var latitude = 0.0

    @set:JsonProperty("lon")
    @JsonProperty("lon")
    var longitude = 0.0
    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is WaypointProjection) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$address`: Any? = address
        val `other$address`: Any? = other.address
        if (if (`this$address` == null) `other$address` != null else `this$address` != `other$address`) return false
        if (java.lang.Double.compare(latitude, other.latitude) != 0) return false
        return if (java.lang.Double.compare(longitude, other.longitude) != 0) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is WaypointProjection
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$address`: Any? = address
        result = result * PRIME + (`$address`?.hashCode() ?: 43)
        val `$latitude` = java.lang.Double.doubleToLongBits(latitude)
        result = result * PRIME + (`$latitude` ushr 32 xor `$latitude`).toInt()
        val `$longitude` = java.lang.Double.doubleToLongBits(longitude)
        result = result * PRIME + (`$longitude` ushr 32 xor `$longitude`).toInt()
        return result
    }

    override fun toString(): String {
        return "WaypointProjection(address=" + address + ", latitude=" + latitude + ", longitude=" + longitude + ")"
    }
}
