/*
package com.example.asuracomic.service;



import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.UnlockedChapter;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UnlockedChapterRepository;
import com.example.asuracomic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;
    private final TransactionRepository transactionRepository;
    private final HttpSession session;

    @Transactional
    public String unlockChapter(Long chapterId) {
        // Lấy thông tin người dùng từ session
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new BadRequestException("Vui lòng đăng nhập để mở khóa chương.");
        }
        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        // Lấy thông tin chương
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BadRequestException("Chương không tồn tại."));

        // Kiểm tra chương có yêu cầu coin không
        if (chapter.isFree()) {
            throw new BadRequestException("Chương này miễn phí, không cần mở khóa.");
        }

        // Kiểm tra số dư coin
        BigDecimal coinPrice = chapter.getCoinPrice();
        if (user.getCoinBalance().compareTo(coinPrice) < 0) {
            throw new BadRequestException("Số dư coin không đủ để mở khóa chương.");
        }

        // Kiểm tra xem chương đã được mở khóa chưa
        boolean isUnlocked = unlockedChapterRepository.existsByUserIdAndChapterId(userId, chapterId);
        if (isUnlocked) {
            throw new BadRequestException("Chương này đã được mở khóa.");
        }

        // Trừ coin từ số dư người dùng
        user.setCoinBalance(user.getCoinBalance().subtract(coinPrice));
        userRepository.save(user);

        // Tạo bản ghi mở khóa chương
        UnlockedChapter unlockedChapter = new UnlockedChapter();
        unlockedChapter.setUser(user);
        unlockedChapter.setChapter(chapter);
        unlockedChapter.setCoinSpent(coinPrice);
        unlockedChapter.setUnlockedAt(LocalDateTime.now());

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.CHAPTER_UNLOCK);
        transaction.setAmount(coinPrice);
        transaction.setChapter(chapter);
        transaction.setTransactionCode(UUID.randomUUID().toString());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        // Lưu bản ghi
        unlockedChapter.setTransaction(transaction);
        transactionRepository.save(transaction);
        unlockedChapterRepository.save(unlockedChapter);

        // Trả về URL chương để chuyển hướng
        return "/asura/comic/" + chapter.getComic().getSlug() + "/chapter/" + chapter.getSlug();
    }
}*/

/*
có ghi lại mỡ khóa chương
*/
/*
package com.example.asuracomic.service;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.UnlockedChapter;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UnlockedChapterRepository;
import com.example.asuracomic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;
    private final TransactionRepository transactionRepository;
    private final HttpSession session;

    @Transactional
    public String unlockChapter(Long chapterId) {
        // Lấy thông tin người dùng từ session
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new BadRequestException("Vui lòng đăng nhập để mở khóa chương.");
        }
        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        // Lấy thông tin chương
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BadRequestException("Chương không tồn tại."));

        // Kiểm tra chương có yêu cầu coin không
        if (chapter.isFree()) {
            throw new BadRequestException("Chương này miễn phí, không cần mở khóa.");
        }

        // Kiểm tra số dư coin
        BigDecimal coinPrice = chapter.getCoinPrice();
        if (user.getCoinBalance().compareTo(coinPrice) < 0) {
            throw new BadRequestException("Số dư coin không đủ để mở khóa chương.");
        }

        // Kiểm tra xem chương đã được mở khóa chưa
        boolean isUnlocked = unlockedChapterRepository.existsByUserIdAndChapterId(userId, chapterId);
        if (isUnlocked) {
            throw new BadRequestException("Chương này đã được mở khóa.");
        }

        // Trừ coin từ số dư người dùng
        user.setCoinBalance(user.getCoinBalance().subtract(coinPrice));
        userRepository.save(user);

        // Tạo bản ghi mở khóa chương
        UnlockedChapter unlockedChapter = new UnlockedChapter();
        unlockedChapter.setUser(user);
        unlockedChapter.setChapter(chapter);
        unlockedChapter.setCoinSpent(coinPrice);
        unlockedChapter.setUnlockedAt(LocalDateTime.now());

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.CHAPTER_UNLOCK);
        transaction.setAmount(coinPrice);
        transaction.setChapter(chapter);
        transaction.setTransactionCode(UUID.randomUUID().toString());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        // Lưu bản ghi
        unlockedChapter.setTransaction(transaction);
        transactionRepository.save(transaction);
        unlockedChapterRepository.save(unlockedChapter);

        // Trả về URL chương để chuyển hướng
        return "/asura/comic/" + chapter.getComic().getSlug() + "/chapter/" + chapter.getSlug();
    }
}*/


package com.example.asuracomic.service;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.UnlockedChapter;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UnlockedChapterRepository;
import com.example.asuracomic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoinService {

    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;
    private final TransactionRepository transactionRepository;
    private final HttpSession session;

    @Transactional
    public String unlockChapter(Long chapterId) {
        // Lấy thông tin người dùng từ session
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new BadRequestException("Vui lòng đăng nhập để mở khóa chương.");
        }
        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại."));

        // Lấy thông tin chương
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BadRequestException("Chương không tồn tại."));

        // Kiểm tra chương có yêu cầu coin không
        if (chapter.isFree()) {
            throw new BadRequestException("Chương này miễn phí, không cần mở khóa.");
        }

        // Kiểm tra số dư coin
        BigDecimal coinPrice = chapter.getCoinPrice();
        if (user.getCoinBalance().compareTo(coinPrice) < 0) {
            throw new BadRequestException("Số dư coin không đủ để mở khóa chương.");
        }

        // Kiểm tra xem chương đã được mở khóa chưa
        boolean isUnlocked = unlockedChapterRepository.existsByUserIdAndChapterId(userId, chapterId);
        if (isUnlocked) {
            throw new BadRequestException("Chương này đã được mở khóa.");
        }

        // Trừ coin từ số dư người dùng
        user.setCoinBalance(user.getCoinBalance().subtract(coinPrice));
        userRepository.save(user);

        // Tạo bản ghi mở khóa chương
        UnlockedChapter unlockedChapter = new UnlockedChapter();
        unlockedChapter.setUser(user);
        unlockedChapter.setChapter(chapter);
        unlockedChapter.setCoinSpent(coinPrice);
        unlockedChapter.setUnlockedAt(LocalDateTime.now());

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.CHAPTER_UNLOCK);
        transaction.setAmount(coinPrice);
        transaction.setChapter(chapter);
        transaction.setTransactionCode(UUID.randomUUID().toString());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        // Lưu bản ghi
        unlockedChapter.setTransaction(transaction);
        transactionRepository.save(transaction);
        unlockedChapterRepository.save(unlockedChapter);

        // Trả về URL chương để chuyển hướng
        return "/asura/comic/" + chapter.getComic().getSlug() + "/chapter/" + chapter.getSlug();
    }
}