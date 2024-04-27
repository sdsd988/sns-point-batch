package me.benny.fcp.point.reservation;


import me.benny.fcp.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointReservationRepository extends JpaRepository<PointReservation,Long> {
}
