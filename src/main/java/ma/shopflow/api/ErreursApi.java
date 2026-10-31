package ma.shopflow.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ma.shopflow.catalogue.ProduitInconnuException;
import ma.shopflow.commande.PaiementRefuseException;
import ma.shopflow.commande.RuptureDeStockException;
import ma.shopflow.panier.QuantiteMaximaleDepasseeException;

/**
 * Traduit les exceptions métier en réponses HTTP au format Problem Details (RFC
 * 9457).
 */
@RestControllerAdvice
public class ErreursApi {

    @ExceptionHandler(ProduitInconnuException.class)
    ProblemDetail produitInconnu(ProduitInconnuException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RuptureDeStockException.class)
    ProblemDetail rupture(RuptureDeStockException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(PaiementRefuseException.class)
    ProblemDetail paiementRefuse(PaiementRefuseException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, e.getMessage());
    }

    @ExceptionHandler(QuantiteMaximaleDepasseeException.class)
    ProblemDetail quantiteMaximale(QuantiteMaximaleDepasseeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
    }
}
