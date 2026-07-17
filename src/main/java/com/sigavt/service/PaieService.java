package com.sigavt.service;

import com.sigavt.dto.request.BulletinPaieRequest;
import com.sigavt.entity.BulletinPaie;

import java.util.List;

public interface PaieService {
    BulletinPaie generer(BulletinPaieRequest request);
    BulletinPaie obtenirParId(Long id);
    List<BulletinPaie> listerParPeriode(String periode);
    BulletinPaie marquerPaye(Long id);
    void supprimer(Long id);
}