package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.config.Constants;
import com.mycompany.myapp.domain.TimeTable;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.TimeTableRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Collections;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.optaplanner.core.api.solver.SolverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class TimeTableResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private TimeTableRepository timeTableRepository;

    private SolverManager<TimeTable, Long> solverManager;

    public TimeTableResource(TimeTableRepository timeTableRepository, SolverManager<TimeTable, Long> solverManager) {
        this.timeTableRepository = timeTableRepository;
        this.solverManager = solverManager;
    }

    /**
     * {@code POST  /solve}
     */
    @PostMapping("/solve")
    public void solve(TimeTableRepository timeTableRepository, SolverManager<TimeTable, Long> solverManager) throws URISyntaxException {
        log.debug("REST request to solve ");
        solverManager.solveAndListen(TimeTableRepository.SINGLETON_TIME_TABLE_ID, timeTableRepository::findById, timeTableRepository::save);
    }
}
