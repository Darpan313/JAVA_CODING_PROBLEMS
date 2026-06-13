package OOP.LiskovSubstitutionPrinciple;

import OOP.LiskovSubstitutionPrinciple.bad.FreeMember;
import OOP.LiskovSubstitutionPrinciple.bad.Member;
import OOP.LiskovSubstitutionPrinciple.bad.PremiumMember;
import OOP.LiskovSubstitutionPrinciple.bad.VipMember;
import OOP.LiskovSubstitutionPrinciple.good.TournamentJoiner;
import OOP.LiskovSubstitutionPrinciple.good.TournamentOrganizer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("\nApproach that does not follow LSP:\n");

        List<Member> members1 = List.of(
                new PremiumMember("Jack Hores"),
                new VipMember("Tom Johns"),
                new FreeMember("Martin Vilop")
        );

        for(Member member : members1) {
            member.joinTournament();
        }
        System.out.println();

//        this code breaks LSP, a free member cannot organize tournament
        for(Member member : members1) {
            member.organizeTournament();
        }

        System.out.println("\nApproach that follow LSP:\n");

        List<TournamentOrganizer> members2 = List.of(
                new OOP.LiskovSubstitutionPrinciple.good.PremiumMember("Jack Hores"),
                new OOP.LiskovSubstitutionPrinciple.good.VipMember("Tom Johns")
        );

        List<TournamentJoiner> members3 = List.of(
                new OOP.LiskovSubstitutionPrinciple.good.PremiumMember("Jack Hores"),
                new OOP.LiskovSubstitutionPrinciple.good.VipMember("Tim Johns"),
                new OOP.LiskovSubstitutionPrinciple.good.FreeMember("Martin Vilop")
        );

//        this code respects LSP
        for(TournamentJoiner member : members3) {
            member.joinTournament();
        }

        System.out.println();

//        this code respects LSP
        for(TournamentOrganizer member : members2) {
            member.organizeTournament();
        }
    }
}
